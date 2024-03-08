package com.fis.invoice.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fis.invoice.domain.Acc;
import com.fis.invoice.domain.GrantRole;
import com.fis.invoice.domain.GrantUser;
import com.fis.invoice.domain.Role;
import com.fis.invoice.domain.Search;
import com.fis.invoice.domain.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AuthService {
	private static final String[] USERATTRIBUTES = { "cn", "name", "samaccountname", "mail" };
	@Value("${ldap.username}")
	private String UN;
	@Value("${ldap.password}")
	private String PW;

	@Autowired
	private JdbcTemplate jt;

	private String toDC(String domain) {
		StringBuilder buf = new StringBuilder();
		for (String token : domain.split("\\.")) {
			if (token.length() == 0)
				continue;
			buf.append(",DC=").append(token);
		}
		return buf.toString().substring(1);
	}

	private class RoleRowMapper implements RowMapper<Role> {
		@Override
		public Role mapRow(ResultSet rs, int rowNum) throws SQLException {
			Role role = new Role(rs.getString("rid"), rs.getString("name"));
			return role;
		}
	}

	private class AccRowMapper implements RowMapper<Acc> {
		@Override
		public Acc mapRow(ResultSet rs, int rowNum) throws SQLException {
			Acc acc = new Acc();
			acc.setUsr(rs.getString("usr"));
			acc.setName(rs.getString("name"));
			acc.setCqt(rs.getString("cqt"));
			acc.setStatus(rs.getString("status"));
			acc.setAdm(rs.getString("adm"));
			acc.setDt(Utils.d2s(rs.getDate("dt")));
			return acc;
		}
	}

	private class UserRowMapper implements RowMapper<User> {
		@Override
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			User user = new User();
			user.setUsr(rs.getString("usr"));
			user.setName(rs.getString("name"));
			user.setCqt(rs.getString("cqt"));
			user.setStatus(rs.getString("status"));
			user.setAdm(rs.getString("adm"));
			return user;
		}
	}

	private String get_aduser(String usr, String domain) throws Exception {
		String un = UN + "@" + domain;
		DirContext ctx = getDirContext(un, PW);
		SearchControls controls = new SearchControls();
		controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		controls.setReturningAttributes(USERATTRIBUTES);
		NamingEnumeration<SearchResult> results = ctx.search(toDC(domain),
				"(&(objectClass=user)(sAMAccountName=" + usr + "))", controls);
		if (results.hasMoreElements()) {
			Attributes attrs = results.nextElement().getAttributes();
			String name = (String) attrs.get("name").get();
			if (StringUtils.isBlank(name))
				name = (String) attrs.get("cn").get();
			if (StringUtils.isBlank(name))
				name = (String) attrs.get("sAMAccountName").get();
			return name;
		}
		return null;
	}

	private void check_user(String cqt) throws Exception {
		String sql = "select a.users-b.cnt cnt from inv_cqt a,(select count(usr) cnt from inv_user where cqt=? and status=1) b where a.ma_cqt=?";
		int cnt = jt.queryForObject(sql, Integer.class, new Object[] { cqt, cqt });
		if (cnt <= 0)
			throw new Exception(
					String.format("Số lượng tài khoản của CQT %s đã vượt quá số lượng tài khoản qui định", cqt));
	}

	public int create_user(Acc acc) throws Exception {
		int rc = 0;
		String sql;
		String cqt = acc.getCqt();
		if (StringUtils.isBlank(cqt))
			cqt = Utils.getCurrentUser().getCqt();
		// String domain = acc.getDomain();
		sql = "select lower(domain) domain from inv_cqt where ma_cqt=?";
		String domain = jt.queryForObject(sql, String.class, new Object[] { cqt });
		if (StringUtils.isBlank(domain))
			throw new Exception(String.format("Không tìm thấy domain của CQT %s", cqt));
		String usr = acc.getUsr();
		String principal = usr + "@" + domain;
		String name = get_aduser(usr, domain);
		if (StringUtils.isBlank(name))
			throw new Exception(String.format("Không tìm thấy %s trên AD", principal));
		check_user(cqt);
		sql = "insert into inv_user(usr,name,cqt) values (?,?,?)";
		rc = jt.update(sql, new Object[] { principal, name, cqt });
		return rc;
	}

	public DirContext getDirContext(String principal, String password) throws Exception {
		String ip = principal.split("@")[1];
		Hashtable<String, Object> env = new Hashtable<String, Object>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, "ldap://" + ip + ":389");
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, principal);
		env.put(Context.SECURITY_CREDENTIALS, password);
		DirContext ctx = new InitialDirContext(env);
		return ctx;
	}

	public UserDetails getUserDetails(String username) throws UsernameNotFoundException {
		try {
			User user = jt.queryForObject("select * from inv_user where usr=?", new UserRowMapper(),
					new Object[] { username });
			if ("2".equals(user.getStatus()))
				throw new Exception("Tài khoản bị khóa");
			List<String> roles = jt.queryForList("select rid from inv_role_user where usr=?", String.class,
					new Object[] { username });
			user.setRoles(roles);
			return user;
		} catch (EmptyResultDataAccessException e) {
			String err = String.format("Tài khoản %s không tồn tại", username);
			log.error(err);
			throw new UsernameNotFoundException(err);
		} catch (Exception e) {
			String err = String.format("Tài khoản %s lỗi : %s", username, e.getMessage());
			log.error(err);
			throw new UsernameNotFoundException(err);
		}
	}

	public int update_user(Acc acc) throws Exception {
		String cqt = acc.getCqt(), status = acc.getStatus();
		if (StringUtils.isBlank(cqt))
			cqt = Utils.getCurrentUser().getCqt();
		if ("1".equals(status))
			check_user(cqt);
		String sql = "update inv_user set cqt=?,status=? where usr=?";
		return jt.update(sql, new Object[] { cqt, status, acc.getUsr() });
	}

	public com.fis.invoice.domain.SearchResult search_user(Search s) throws Exception {
		List<Acc> list = null;
		int total = 0;
		try {
			String cqt = s.getCqt(), usr = s.getUsr(), name = s.getName(), status = s.getStatus();
			if (StringUtils.isBlank(cqt))
				cqt = Utils.getCurrentUser().getCqt();
			StringBuffer wh = new StringBuffer(" where cqt=?");
			List<Object> param = new ArrayList<Object>();
			param.add(cqt);
			if (StringUtils.isNotBlank(status)) {
				wh.append(" and status=?");
				param.add(Integer.parseInt(status));	
			}
			if (StringUtils.isNotBlank(usr)) {
				wh.append(" and upper(usr) like ?");
				param.add("%" + usr.trim().toUpperCase() + "%");
			}
			if (StringUtils.isNotBlank(name)) {
				wh.append(" and upper(name) like ?");
				param.add("%" + name.trim().toUpperCase() + "%");
			}
			Object[] obj = param.toArray(new Object[0]);
			StringBuffer sb = new StringBuffer("select count(1) from inv_user");
			sb.append(wh);
			total = jt.queryForObject(sb.toString(), Integer.class, obj);
			if (total > 0) {
				int page = s.getPage(), limit = s.getLimit(), offset = page * limit;
				String order = s.getOrder();
				sb = new StringBuffer("select * from inv_user");
				sb.append(wh);
				if (StringUtils.isNotBlank(order))
					sb.append(" order by " + order);
				sb.append(" offset " + offset + " rows fetch next " + limit + " rows only");
				list = jt.query(sb.toString(), new AccRowMapper(), obj);
			} else {
				list = new ArrayList<Acc>();
			}

		} catch (Exception e) {
			log.error("search_user", e);
			throw e;
		}
		return new com.fis.invoice.domain.SearchResult(total, list);
	}

	@Cacheable(value = "ROLES", key = "0")
	public List<Role> get_roles() {
		String sql = "select * from inv_role";
		List<Role> result = jt.query(sql, new RoleRowMapper());
		return result;
	}

	public List<String> get_role_by_user(String usr) {
		String sql = "select rid from inv_role_user where usr=?";
		List<String> result = jt.queryForList(sql, String.class, new Object[] { usr });
		return result;
	}

	public List<String> get_user_by_role(GrantRole gr) {
		final String rid = gr.getRid();
		List<String> usrs = gr.getUsrs();
		final String str = String.join(",", Collections.nCopies(usrs.size(), "?"));
		String sql = String.format("select usr from inv_role_user where rid=? and usr in (%s)", str);
		usrs.add(0, rid);
		List<String> result = jt.queryForList(sql, String.class, usrs.toArray());
		return result;
	}

	@Transactional
	public void grant_by_user(GrantUser gu) {
		final String usr = gu.getUsr();
		List<String> rids = gu.getRids();
		jt.update("delete from inv_role_user where usr=?", new Object[] { usr });
		if (Utils.isEmpty(rids))
			return;
		jt.batchUpdate("insert into inv_role_user(usr,rid) values (?,?)", new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setString(1, usr);
				ps.setString(2, rids.get(i));
			}

			public int getBatchSize() {
				return rids.size();
			}

		});
	}

	@Transactional
	public void grant_by_role(GrantRole gr) {
		final String rid = gr.getRid();
		List<String> usrs = gr.getUsrs();
		List<String> list = gr.getList();
		if (!Utils.isEmpty(list)) {
			jt.batchUpdate("delete from inv_role_user where usr=? and rid=?", new BatchPreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					ps.setString(1, list.get(i));
					ps.setString(2, rid);
				}

				public int getBatchSize() {
					return list.size();
				}

			});
		}
		if (Utils.isEmpty(usrs))
			return;
		jt.batchUpdate("insert into inv_role_user(usr,rid) values (?,?)", new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setString(1, usrs.get(i));
				ps.setString(2, rid);
			}

			public int getBatchSize() {
				return usrs.size();
			}
		});
	}
}