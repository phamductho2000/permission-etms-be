package com.fis.invoice.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.fis.invoice.domain.Dobj;
import com.fis.invoice.domain.Dsearch;
import com.fis.invoice.domain.Dtl;
import com.fis.invoice.domain.Err;
import com.fis.invoice.domain.Hdr;
import com.fis.invoice.domain.Msg;
import com.fis.invoice.domain.Search;
import com.fis.invoice.domain.SearchResult;

@Service
public class InvService {
	@Autowired
	private JdbcTemplate jt;

	private class DtlRowMapper implements RowMapper<Dtl> {
		@Override
		public Dtl mapRow(ResultSet rs, int rowNum) throws SQLException {
			Dtl d = new Dtl();
			d.setRn(rowNum);
			d.setStt(rs.getString("stt"));
			d.setTchat(rs.getString("tchat"));
			d.setLdchinh(rs.getString("ldchinh"));
			d.setMhhdvu(rs.getString("mhhdvu"));
			d.setTen(rs.getString("ten"));
			d.setSluong(rs.getDouble("sluong"));
			d.setDvtinh(rs.getString("dvtinh"));
			d.setDgia(rs.getDouble("dgia"));
			d.setThtien(rs.getDouble("thtien"));
			d.setLtsuat(rs.getString("ltsuat"));
			d.setTsuat(rs.getDouble("tsuat"));
			d.setTlckhau(rs.getDouble("tlckhau"));
			d.setStckhau(rs.getDouble("stckhau"));
			return d;
		}
	}

	private class HdrRowMapper implements RowMapper<Hdr> {
		@Override
		public Hdr mapRow(ResultSet rs, int rowNum) throws SQLException {
			Hdr h = new Hdr();
			h.setId(rs.getString("id"));
			h.setTdlap(Utils.d2s(rs.getDate("tdlap")));
			h.setCqt(rs.getString("cqt"));
			h.setTthai(rs.getString("tthai"));
			h.setMhdon(rs.getString("mhdon"));
			h.setDksbke(rs.getString("dksbke"));
			h.setDknlbke(Utils.d2s(rs.getDate("dknlbke")));
			String tchat = rs.getString("tchat");
			h.setTchat(tchat);
			h.setKhmshdon(rs.getString("khmshdon"));
			h.setKhhdon(rs.getString("khhdon"));
			h.setShdon(rs.getString("shdon"));
			if (!"1".equals(tchat)) {
				h.setKhmshdgoc(rs.getString("khmshdgoc"));
				h.setKhhdgoc(rs.getString("khhdgoc"));
				h.setShdgoc(rs.getString("shdgoc"));
			}
			h.setNbmst(rs.getString("nbmst"));
			h.setNbten(rs.getString("nbten"));
			h.setNbdchi(rs.getString("nbdchi"));
			h.setNbdctdtu(rs.getString("nbdctdtu"));
			h.setNbsdthoai(rs.getString("nbsdthoai"));
			h.setNbstkhoan(rs.getString("nbstkhoan"));
			h.setNbtnhang(rs.getString("nbtnhang"));

			h.setNmmst(rs.getString("nmmst"));
			h.setNmten(rs.getString("nmten"));
			h.setNmdchi(rs.getString("nmdchi"));
			h.setNmdctdtu(rs.getString("nmdctdtu"));
			h.setNmsdthoai(rs.getString("nmsdthoai"));
			h.setNmstkhoan(rs.getString("nmstkhoan"));
			h.setNmtnhang(rs.getString("nmtnhang"));
			h.setNmtnmua(rs.getString("nmtnmua"));

			h.setDvtte(rs.getString("dvtte"));
			h.setTgia(rs.getDouble("tgia"));
			h.setHtttoan(rs.getString("htttoan"));

			h.setTgtcthue(rs.getDouble("tgtcthue"));
			h.setTgtthue(rs.getDouble("tgtthue"));
			h.setTgtttbso(rs.getDouble("tgtttbso"));
			h.setTtcktmai(rs.getDouble("ttcktmai"));
			h.setTgtphi(rs.getDouble("tgtphi"));

			h.setPct10_sothue(rs.getDouble("pct10_sothue"));
			h.setPct10_sotien(rs.getDouble("pct10_sotien"));
			h.setPct5_sothue(rs.getDouble("pct5_sothue"));
			h.setPct5_sotien(rs.getDouble("pct5_sotien"));
			h.setPct8_sothue(rs.getDouble("pct8_sothue"));
			h.setPct8_sotien(rs.getDouble("pct8_sotien"));
			h.setPctkhac_sothue(rs.getDouble("pctkhac_sothue"));
			h.setPctkhac_sotien(rs.getDouble("pctkhac_sotien"));
			h.setPctkct_sotien(rs.getDouble("pctkct_sotien"));
			h.setPctkkknt_sotien(rs.getDouble("pctkkknt_sotien"));
			h.setPct0_sotien(rs.getDouble("pct0_sotien"));
			return h;
		}
	}

	private class ErrRowMapper implements RowMapper<Err> {
		@Override
		public Err mapRow(ResultSet rs, int rowNum) throws SQLException {
			Err e = new Err();
			e.setDt(Utils.d2s(rs.getDate("dt")));
			e.setErr(rs.getString("err"));
			e.setIdhdon(rs.getString("idhdon"));
			e.setStt(rs.getString("stt"));
			e.setNbmst(rs.getString("nbmst"));
			e.setKhmshdon(rs.getString("khmshdon"));
			e.setKhhdon(rs.getString("khhdon"));
			e.setShdon(rs.getString("shdon"));
			e.setType(rs.getString("type"));
			return e;
		}
	}

	public List<Dobj> chart(Dsearch s) throws Exception {
		String cqt = s.getCqt(), mst = s.getMst(), lky = s.getLky(), fd = s.getFd(), td = s.getTd(), val = s.getVal(),
				col, aol;
		List<Object> param = new ArrayList<Object>();
		param.add(cqt);
		param.add(mst);
		String tu, den;
		if ("month".equals(lky)) {
			col = "mm";
			aol = "a.mm";
			tu = Utils.yymm(fd);
			den = Utils.yymm(td);
			param.add(tu);
			param.add(den);
			param.add(tu);
			param.add(den);
			param.add(tu);
			param.add(den);
		} else {
			col = "qq";
			aol = "a.qq";
			tu = Utils.yyq(fd);
			den = Utils.yyq(td);
			param.add(tu);
			param.add(den);
			param.add(tu);
			param.add(den);
			param.add(tu);
			param.add(den);
		}
		Object[] objs = param.toArray(new Object[0]);
		String wh1 = " where a.mst=b.tin and " + aol + " between ? and ?";
		String wh2 = " and a.khmshdon in (1,5) and a.tthai in (1,2,3,5) and a.ttxly in (5,6,8)";
		String grb = " group by " + aol + ")";
		StringBuffer sb = new StringBuffer("with tin as (select tin from mv_tin where taxo=? and tin=?)");
		sb.append(",tks as (select 'Kê khai' typ," + aol + " prd,sum(a." + val + ") val from  mv_tk_" + col + " a,tin b"
				+ wh1 + grb);
		sb.append(",sls as (select 'Bán' typ," + aol + " prd,sum(a." + val + ") val from mv_sls_" + col + " a,tin b"
				+ wh1 + wh2 + grb);
		sb.append(",buy as (select 'Mua' typ," + aol + " prd,sum(a." + val + ") val from mv_buy_" + col + " a,tin b"
				+ wh1 + wh2 + grb);
		sb.append(
				"select typ,prd,round(val/1000000,3) val from (select * from tks union select * from sls union select * from buy) order by typ,prd");
		List<Dobj> list = jt.query(sb.toString(), BeanPropertyRowMapper.newInstance(Dobj.class), objs);
		return list;
	}

	public SearchResult search_invoice(Search s) throws Exception {
		String cqt = Utils.getCurrentUser().getCqt();
		String fd = s.getFd(), td = s.getTd(), khmshdon = s.getKhmshdon(), khhdon = s.getKhhdon(), shdon = s.getShdon(),
				tthai = s.getTthai(), nbmst = s.getNbmst(), nmmst = s.getNmmst(), tchat = s.getTchat(),
				nbten = s.getNbten(), nmten = s.getNmten();
		StringBuffer wh = new StringBuffer(" where tdlap between ? and ? and cqt=? and khmshdon=?");
		List<Object> param = new ArrayList<Object>();
		param.add(Utils.s2d(fd));
		param.add(Utils.s2nextdate(td));
		param.add(cqt);
		param.add(khmshdon);
		if (StringUtils.isNotBlank(khhdon)) {
			wh.append(" and khhdon=?");
			param.add(khhdon);
		}
		if (StringUtils.isNotBlank(shdon)) {
			wh.append(" and shdon=?");
			param.add(shdon);
		}
		if (!"0".equals(tchat)) {
			wh.append(" and tchat=?");
			param.add(Integer.parseInt(tchat));
		}
		if (!"0".equals(tthai)) {
			wh.append(" and tthai=?");
			param.add(Integer.parseInt(tthai));
		}
		if (StringUtils.isNotBlank(nbmst)) {
			List<String> tins = new ArrayList<String>(Arrays.asList(nbmst.split(",")));
			wh.append(String.format(" and nbmst in (%s)", String.join(",", Collections.nCopies(tins.size(), "?"))));
			param.addAll(tins);
		}
		if (StringUtils.isNotBlank(nmmst)) {
			List<String> tins = new ArrayList<String>(Arrays.asList(nmmst.split(",")));
			wh.append(String.format(" and nmmst in (%s)", String.join(",", Collections.nCopies(tins.size(), "?"))));
			param.addAll(tins);
		}
		if (StringUtils.isNotBlank(nbten)) {
			wh.append(" and upper(nbten) like ?");
			param.add("%" + nbten.trim().toUpperCase() + "%");
		}
		if (StringUtils.isNotBlank(nmten)) {
			wh.append(" and upper(nmten) like ?");
			param.add("%" + nmten.trim().toUpperCase() + "%");
		}

		Object[] obj = param.toArray(new Object[0]);
		StringBuffer sb = new StringBuffer("select /*+ PARALLEL(8) */ count(id) from inv_hdr");
		sb.append(wh);
		int total = jt.queryForObject(sb.toString(), Integer.class, obj);
		List<Hdr> list;
		if (total > 0) {
			int page = s.getPage(), limit = s.getLimit(), offset = page * limit;
			String order = s.getOrder();
			sb = new StringBuffer("select /*+ PARALLEL(8) */ * from inv_hdr");
			sb.append(wh);
			if (StringUtils.isNotBlank(order))
				sb.append(" order by " + order);
			sb.append(" offset " + offset + " rows fetch next " + limit + " rows only");
			list = jt.query(sb.toString(), new HdrRowMapper(), obj);
		} else
			list = new ArrayList<Hdr>();
		return new SearchResult(total, list);
	}

	public List<Dtl> get_dtl(String id) throws Exception {
		return jt.query("select * from inv_dtl where idhdon=? order by stt", new DtlRowMapper(), new Object[] { id });
	}

	public SearchResult search_err(Search s) throws Exception {
		String cqt = s.getCqt(), dt = s.getDt(), khmshdon = s.getKhmshdon(), khhdon = s.getKhhdon(),
				shdon = s.getShdon(), nbmst = s.getNbmst(), source = s.getSource();
		StringBuffer wh = new StringBuffer(" where trunc(dt)=? and cqt=? and khmshdon=?");
		List<Object> param = new ArrayList<Object>();
		param.add(Utils.s2d(dt));
		param.add(cqt);
		param.add(khmshdon);
		if (StringUtils.isNotBlank(khhdon)) {
			wh.append(" and khhdon=?");
			param.add(khhdon);
		}
		if (StringUtils.isNotBlank(shdon)) {
			wh.append(" and shdon=?");
			param.add(shdon);
		}
		if (StringUtils.isNotBlank(nbmst)) {
			List<String> tins = new ArrayList<String>(Arrays.asList(nbmst.split(",")));
			wh.append(String.format(" and nbmst in (%s)", String.join(",", Collections.nCopies(tins.size(), "?"))));
			param.addAll(tins);
		}
		Object[] obj = param.toArray(new Object[0]);
		StringBuffer sb = new StringBuffer("select count(*) from " + source + "_err");
		sb.append(wh);
		int total = jt.queryForObject(sb.toString(), Integer.class, obj);
		List<Err> list;
		if (total > 0) {
			int page = s.getPage(), limit = s.getLimit(), offset = page * limit;
			String order = s.getOrder();
			sb = new StringBuffer("select * from " + source + "_err");
			sb.append(wh);
			if (StringUtils.isNotBlank(order))
				sb.append(" order by " + order);
			sb.append(" offset " + offset + " rows fetch next " + limit + " rows only");
			list = jt.query(sb.toString(), new ErrRowMapper(), obj);
		} else
			list = new ArrayList<Err>();
		return new SearchResult(total, list);
	}

	public SearchResult search_log(Search s) throws Exception {
		String cqt = s.getCqt(), dt = s.getDt(), khmshdon = s.getKhmshdon(), khhdon = s.getKhhdon(),
				shdon = s.getShdon(), nbmst = s.getNbmst(), source = s.getSource();
		StringBuffer wh = new StringBuffer(" where trunc(dt)=? and cqt=? and khmshdon=?");
		List<Object> param = new ArrayList<Object>();
		param.add(Utils.s2d(dt));
		param.add(cqt);
		param.add(khmshdon);
		if (StringUtils.isNotBlank(khhdon)) {
			wh.append(" and khhdon=?");
			param.add(khhdon);
		}
		if (StringUtils.isNotBlank(shdon)) {
			wh.append(" and shdon=?");
			param.add(shdon);
		}
		if (StringUtils.isNotBlank(nbmst)) {
			List<String> tins = new ArrayList<String>(Arrays.asList(nbmst.split(",")));
			wh.append(String.format(" and nbmst in (%s)", String.join(",", Collections.nCopies(tins.size(), "?"))));
			param.addAll(tins);
		}
		Object[] obj = param.toArray(new Object[0]);
		StringBuffer sb = new StringBuffer("select count(*) from " + source + "_log");
		sb.append(wh);
		int total = jt.queryForObject(sb.toString(), Integer.class, obj);
		List<Msg> list;
		if (total > 0) {
			int page = s.getPage(), limit = s.getLimit(), offset = page * limit;
			String order = s.getOrder();
			sb = new StringBuffer("select nbmst,khhdon,shdon,dt from " + source + "_log");
			sb.append(wh);
			if (StringUtils.isNotBlank(order))
				sb.append(" order by " + order);
			sb.append(" offset " + offset + " rows fetch next " + limit + " rows only");
			list = jt.query(sb.toString(), BeanPropertyRowMapper.newInstance(Msg.class), obj);
		} else
			list = new ArrayList<Msg>();
		return new SearchResult(total, list);
	}
}