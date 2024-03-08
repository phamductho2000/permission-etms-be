package com.fis.invoice.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.fis.invoice.domain.Kv;
import com.fis.invoice.domain.Node;
import com.fis.invoice.domain.Search;
import com.fis.invoice.domain.SearchResult;
import com.fis.invoice.domain.Tin;

//import lombok.extern.slf4j.Slf4j;

@Service
public class SysService {
	@Autowired
	private JdbcTemplate jt;

	private class KvRowMapper implements RowMapper<Kv> {
		@Override
		public Kv mapRow(ResultSet rs, int rowNum) throws SQLException {
			Kv kv = new Kv(rs.getString(1), rs.getString(2));
			return kv;
		}
	}

	private class TinRowMapper implements RowMapper<Tin> {
		@Override
		public Tin mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new Tin(rs.getString(1), rs.getString(2), rs.getString(3));
		}
	}

	@Cacheable(value = "REPORTS", key = "0")
	public List<Kv> get_reports() {
		String sql = "select rid,name from inv_role where rid like 'r%' and rid<>'r9'";
		List<Kv> result = jt.query(sql, new KvRowMapper());
		return result;
	}

	@Cacheable(value = "CUC_THUE", key = "0")
	public List<Kv> get_cuc_thue() {
		String sql = "select ma_cqt,ten_cqt_dai from ztb_map_cqt where MANDT='500' and ma_cha_4='0000' and ma_cqt like '%00' order by ma_cqt";
		List<Kv> result = jt.query(sql, new KvRowMapper());
		return result;
	}

	public String get_ten_cuc_thue(String cuc) {
		return Utils.find(get_cuc_thue(), cuc);
	}

	public String get_ten_cqt(String cqt) {
		return Utils.find(get_cqt(cqt), cqt);
	}

	public String get_ten_lhkt(String lhkt) {
		return Utils.find(get_lhkt(), lhkt);
	}

	public String get_ten_lnnt(String lnnt) {
		return Utils.find(get_lnnt(), lnnt);
	}

	@Cacheable(value = "CQT")
	public List<Kv> get_cqt(String cqt) {
		String sql;
		List<Kv> result;
		if ("0000".equals(cqt)) {
			sql = "select ma_cqt,ten_cqt_dai from ztb_map_cqt where MANDT='500' and  ma_cqt='0000'";
			result = jt.query(sql, new KvRowMapper());
		} else {
			sql = "select ma_cqt,ten_cqt_dai from ztb_map_cqt where MANDT='500' and ma_cha_4=? or ma_cqt=? order by ma_cqt";
			result = jt.query(sql, new KvRowMapper(), new Object[] { cqt, cqt });
		}
		return result;
	}

	@Cacheable(value = "CQT_SELECT")
	public List<Kv> get_cqt_select(String cqt) {
		String sql;
		List<Kv> result;
		if ("0000".equals(cqt)) {
			sql = "select ma_cqt,ma_cqt||'-'||ten_cqt_dai from ztb_map_cqt where MANDT='500' and  ma_cqt='0000'";
			result = jt.query(sql, new KvRowMapper());
		} else {
			sql = "select ma_cqt,ma_cqt||'-'||ten_cqt_dai from ztb_map_cqt where MANDT='500' and ma_cha_4=? or ma_cqt=? order by ma_cqt";
			result = jt.query(sql, new KvRowMapper(), new Object[] { cqt, cqt });
		}
		return result;
	}

	@Cacheable(value = "LHKT", key = "0")
	public List<Kv> get_lhkt() {
		String sql = "select ma,ten from dm_lhkt order by ma";
		List<Kv> result = jt.query(sql, new KvRowMapper());
		return result;
	}

	@Cacheable(value = "LNNT", key = "0")
	public List<Kv> get_lnnt() {
		String sql = "select loai,ten from dm_loai_nnt order by ten";
		List<Kv> result = jt.query(sql, new KvRowMapper());
		return result;
	}

	private List<Node> nnkd(String sql) {
		List<Node> result = new ArrayList<>();
		List<Map<String, Object>> rows = jt.queryForList(sql);
		Map<String, Node> map = new HashMap<>();
		for (Map<String, Object> row : rows) {
			String key = (String) row.get("MA_NNKD");
			String title = key + "." + (String) row.get("TEN_NNKD");
			int len = key.length();
			if (len == 1) {
				Node node = new Node();
				node.setValue(key);
				node.setTitle(title);
				map.put(key, node);
			} else {
				String pid = len == 3 ? key.substring(0, 1) : key.substring(0, 3);
				Node node = new Node();
				node.setValue(key);
				node.setPid(pid);
				node.setTitle(title);
				if (len == 3)
					map.put(key, node);
				Node root = map.get(pid);
				if (root != null) {
					List<Node> children = root.getChildren();
					if (Utils.isEmpty(children))
						children = new ArrayList<>();
					children.add(node);
					root.setChildren(children);
				}
			}
		}
		for (Entry<String, Node> entry : map.entrySet()) {
			Node node = entry.getValue();
			if (StringUtils.isBlank(node.getPid()))
				result.add(node);
		}
		return result;
	}

	@Cacheable(value = "NNKD")
	public List<Node> get_nnkd(String type) {
		String sql = "1".equals(type)
				? "select ma_nnkd,ten_nnkd from dm_nganhktqd where ma_nnkd not like '%.' and length(ma_nnkd)<=4 order by ma_nnkd"
				: "select substr(ma_nnkd,1,length(ma_nnkd)-1) ma_nnkd, ten_nnkd from dm_nganhktqd where ma_nnkd like '%.' and length(ma_nnkd)<=5 order by ma_nnkd";
		List<Node> result = nnkd(sql);
		return result;
	}

	public SearchResult search_nnt(Search s) throws Exception {
		String cqt = s.getCqt(), mst = s.getMst(), name = s.getName();
		StringBuffer wh = new StringBuffer(" where 1=1");
		List<Object> param = new ArrayList<Object>();
		if ("0".equals(cqt)) {
			String cucthue = s.getCucthue();
			if (!"0000".equals(cucthue)) {
				wh.append(
						" and taxo in (select ma_cqt from ztb_map_cqt where MANDT='500' and (ma_cqt=? or ma_cha_4=?))");
				param.add(cucthue);
				param.add(cucthue);
			}
		} else {
			wh.append(" and taxo=?");
			param.add(cqt);
		}
		if (StringUtils.isNotBlank(mst)) {
			int len = mst.length();
			if (len == 10 || len == 14) {
				wh.append(" and tin=?");
				param.add(mst);
			} else {
				wh.append(" and tin like ?");
				param.add("%" + mst + "%");
			}
		}
		if (StringUtils.isNotBlank(name)) {
			wh.append(" and upper(oname) like ?");
			param.add("%" + name.trim().toUpperCase() + "%");
		}
		Object[] objs = param.toArray(new Object[0]);
		//log.info(Arrays.toString(objs));
		StringBuffer sb = new StringBuffer("select /*+ PARALLEL(8) */ count(tin) from mv_tin");
		sb.append(wh);
		String sql = sb.toString();
		//log.info(sql);
		int total = jt.queryForObject(sql, Integer.class, objs);
		List<Tin> list;
		if (total > 0) {
			int page = s.getPage(), limit = s.getLimit(), offset = page * limit;
			String order = s.getOrder();
			sb = new StringBuffer("select tin,oname,taxo from mv_tin");
			sb.append(wh);
			if (StringUtils.isNotBlank(order))
				sb.append(" order by " + order);
			sb.append(" offset " + offset + " rows fetch next " + limit + " rows only");
			sql = sb.toString();
			//log.info(sql);
			list = jt.query(sql, new TinRowMapper(), objs);
		} else
			list = new ArrayList<Tin>();
		return new SearchResult(total, list);
	}

}