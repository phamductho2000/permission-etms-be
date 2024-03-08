package com.fis.invoice.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.fis.invoice.domain.Report;
import com.fis.invoice.domain.Search;
import com.fis.invoice.domain.SearchResult;
import com.opencsv.CSVWriter;

import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;

@Slf4j
@Service
public class RepService {
	@Autowired
	private JdbcTemplate jt;
	@Autowired
	private Excel excel;
	@Autowired
	private SysService sys;
	@Value(value = "${xls.path}")
	private String PATH;
	@Value(value = "${threads}")
	private int THREADS;
	private static ExecutorService ES;
	private static BlockingQueue<Report> BQ = new LinkedBlockingQueue<>();

	public void put(Report r) throws InterruptedException {
		BQ.put(r);
	}

	@PostConstruct
	private void init() {
		ES = Executors.newFixedThreadPool(THREADS);
		for (int i = 0; i < THREADS; i++) {
			ES.submit(() -> {
				while (true) {
					try {
						Report r = BQ.take();
						process(r);
					} catch (InterruptedException e) {
						log.error("InterruptedException:", e);
					}
				}
			});
		}
	}

	private String ten_cqt_cha(String cqt) {
		if ("0000".equals(cqt))
			return "";
		return cqt.endsWith("00") ? "Tổng Cục Thuế" : sys.get_ten_cuc_thue(cqt.substring(0, 2) + "00");
	}

	private void insrep(Report r) {
		Timestamp dt = Timestamp.valueOf(r.getDt());
		Date fd = Utils.s2d(r.getFd()), td = Utils.s2d(r.getTd());
		String usr = r.getUsr(), code = r.getCode(), cqt = r.getCqt(), sql;
		sql = "insert into inv_rep(usr,code,cqt,fd,td,xls,ft) values (?,?,?,?,?,?,?)";
		jt.update(sql, new Object[] { usr, code, cqt, fd, td, r.getXls(), dt });
		sql = "insert into inv_rep_log(usr,code,cqt,fd,td,dt) values (?,?,?,?,?,?)";
		jt.update(sql, new Object[] { usr, code, cqt, fd, td, dt });
	}

	private void insrep(Report r, String err) {
		Timestamp dt = Timestamp.valueOf(r.getDt());
		Date fd = Utils.s2d(r.getFd()), td = Utils.s2d(r.getTd());
		String usr = r.getUsr(), code = r.getCode(), cqt = r.getCqt(), sql;
		sql = "insert into inv_rep(usr,code,cqt,fd,td,err,ft) values (?,?,?,?,?,?,?)";
		jt.update(sql, new Object[] { usr, code, cqt, fd, td, err, dt });
		sql = "insert into inv_rep_log(usr,code,cqt,fd,td,dt) values (?,?,?,?,?,?)";
		jt.update(sql, new Object[] { usr, code, cqt, fd, td, dt });
	}

	private void xls(Report r, String sql, Object[] objs, String[] header, int rf) {
		jt.query(sql, new ResultSetExtractor<List<?>>() {
			public List<?> extractData(ResultSet rs) throws SQLException {
				String xls = r.getXls();
				try (ZipFile zf = new ZipFile(PATH + xls + ".zip")) {
					byte[] bytes = excel.createWorkbook(r.getCode(), header, rs, rf);
					ZipParameters zp = new ZipParameters();
					zp.setFileNameInZip(xls + ".xlsx");
					zf.addStream(new ByteArrayInputStream(bytes), zp);
					insrep(r);
				} catch (Exception e) {
					log.error("XLS:", e);
					insrep(r, e.getMessage());
				}
				return null;
			}
		}, objs);
	}

	private void csv(Report r, String sql, Object[] objs) {
		jt.query(sql, new ResultSetExtractor<List<?>>() {
			public List<?> extractData(ResultSet rs) throws SQLException {
				String xls = r.getXls();
				try (ZipFile zf = new ZipFile(PATH + xls + ".zip");
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						OutputStreamWriter osw = new OutputStreamWriter(baos);
						CSVWriter csvw = new CSVWriter(osw);) {
					csvw.writeAll(rs, true);
					osw.flush();
					ZipParameters zp = new ZipParameters();
					zp.setFileNameInZip(xls + ".csv");
					zf.addStream(new ByteArrayInputStream(baos.toByteArray()), zp);
					insrep(r);
				} catch (Exception e) {
					log.error("CSV:", e);
					insrep(r, e.getMessage());
				}
				return null;
			}
		}, objs);
	}

	private class ReportRowMapper implements RowMapper<Report> {
		@Override
		public Report mapRow(ResultSet rs, int rowNum) throws SQLException {
			Report r = new Report();
			r.setRn(rowNum);
			r.setId(rs.getLong("id"));
			r.setCode(rs.getString("code"));
			r.setCqt(rs.getString("cqt"));
			r.setXls(rs.getString("xls"));
			r.setErr(rs.getString("err"));
			r.setFd(Utils.d2s(rs.getDate("fd")));
			r.setTd(Utils.d2s(rs.getDate("td")));
			r.setFt(Utils.timestamp2s(rs.getTimestamp("ft")));
			r.setTt(Utils.timestamp2s(rs.getTimestamp("tt")));
			r.setElapsed(rs.getLong("elapsed"));
			r.setName(rs.getString("name"));
			return r;
		}
	}

	public int del(String id) {
		String usr = Utils.getCurrentUser().getUsername();
		return jt.update("delete from inv_rep where id=? and usr=?", new Object[] { id, usr });
	}

	public SearchResult search(Search s) throws Exception {
		String usr = Utils.getCurrentUser().getUsername();
		String fd = s.getFd(), td = s.getTd(), code = s.getCode();
		StringBuffer wh = new StringBuffer(" where a.ft between ? and ? and a.usr=?");
		List<Object> param = new ArrayList<Object>();
		param.add(Utils.s2d(fd));
		param.add(Utils.s2nextdate(td));
		param.add(usr);
		if (!"0".equals(code)) {
			wh.append(" and a.code=?");
			param.add(code);
		}
		Object[] obj = param.toArray(new Object[0]);
		StringBuffer sb = new StringBuffer("select count(*) from inv_rep a");
		sb.append(wh);
		int total = jt.queryForObject(sb.toString(), Integer.class, obj);
		List<Report> list;
		if (total > 0) {
			int page = s.getPage(), limit = s.getLimit(), offset = page * limit;
			String order = s.getOrder();
			sb = new StringBuffer(
					"select a.id,a.usr,a.code,a.cqt,a.fd,a.td,a.xls,a.err,a.ft,a.tt,a.elapsed,b.name from inv_rep a,inv_role b");
			sb.append(wh);
			sb.append(" and a.code=b.rid ");
			if (StringUtils.isNotBlank(order)) {
				sb.append(" order by " + order);
			} else
				sb.append(" order by ft desc");
			sb.append(" offset " + offset + " rows fetch next " + limit + " rows only");
			list = jt.query(sb.toString(), new ReportRowMapper(), obj);
		} else
			list = new ArrayList<Report>();
		return new SearchResult(total, list);

	}

	private void r1_2(Report r) {
		String fd = r.getFd(), cqt = r.getCqt(), lbc = r.getLbc(), mst = r.getMst(), lky = r.getLky();
		String ltk = r.getLtk(), pk, col, tk, hd, p2, p3, bcode, bname, btab, acode, ten = "Tất cả";
		if ("month".equals(lky)) {
			pk = Utils.yymm(fd);
			col = "mm";
			tk = "mv_tk_mm";
			hd = "1".equals(ltk) ? "mv_sls_mm" : "mv_buy_mm";
			p2 = "Tháng: " + Utils.mmyyyy(fd);
		} else {
			pk = Utils.yyq(fd);
			col = "qq";
			tk = "mv_tk_qq";
			hd = "1".equals(ltk) ? "mv_sls_qq" : "mv_buy_qq";
			p2 = "Quý: " + Utils.qqyyyy(fd);
		}
		StringBuffer wh = new StringBuffer(" where a.taxo=?");
		List<Object> param = new ArrayList<Object>();
		param.add(cqt);
		if (StringUtils.isNotBlank(mst)) {
			List<String> tins = new ArrayList<String>(Arrays.asList(mst.split(",")));
			wh.append(String.format(" and tin in (%s)", String.join(",", Collections.nCopies(tins.size(), "?"))));
			param.addAll(tins);
		}
		switch (lbc) {
		case "1":
			String lhkt = r.getLhkt();
			if (!"0".equals(lhkt)) {
				param.add(lhkt);
				wh.append(" and a.legal_enty=?");
				ten = sys.get_ten_lhkt(lhkt);
			}
			p3 = String.format("LHKT: %s ", ten);
			bcode = "ma";
			bname = "ten";
			btab = "dm_lhkt";
			acode = "legal_enty";
			break;
		case "2":
			String lnnt = r.getLnnt();
			if (!"0".equals(lnnt)) {
				param.add(lnnt);
				wh.append(" and a.bpkind=?");
				ten = sys.get_ten_lnnt(lnnt);
			}
			p3 = String.format("Loại NNT: %s ", ten);
			bcode = "loai";
			bname = "ten";
			btab = "dm_loai_nnt";
			acode = "bpkind";
			break;
		default:
			List<String> nnkds = r.getNnkd();
			if (!Utils.isEmpty(nnkds)) {
				StringBuffer nn = new StringBuffer();
				StringBuffer sb = new StringBuffer();
				for (String nnkd : nnkds) {
					nn.append("," + nnkd);
					param.add(nnkd + "%");
					sb.append(" or a.ind_sector like ?");
				}
				wh.append(" and (");
				wh.append(sb.substring(3));
				wh.append(")");
				ten = nn.substring(1);
			}
			p3 = String.format("NNKD: %s ", ten);
			bcode = "ma_nnkd";
			bname = "ten_nnkd";
			btab = "dm_nganhktqd";
			acode = "ind_sector";
		}
		param.add(pk);
		param.add(pk);
		// @formatter:off
		StringBuffer sb = new StringBuffer("with tin as (select a.tin mst,a.oname name,b." + bname + " ten from mv_tin a," + btab + " b" + wh + " and a." + acode + "=b." + bcode + ")");
		if ("1".equals(ltk)) {
			sb.append(",tk as (select tin.mst,sum(amt) da,sum(tax) dt,sum(amt_kct) dakct,sum(amt_kkk) dakkk,sum(amt_0) da0,sum(amt_5) da5,sum(tax_5) dt5,sum(amt_10) da10,sum(tax_10) dt10 from tin," + tk + " where tin.mst=" + tk + ".mst and " + col + "=? group by tin.mst)");
			sb.append(",hd as (select a.mst,sum(amt) a,sum(tax) t,sum(amt_kct) akct,sum(amt_kkk) akkk,sum(amt_0) a0,sum(amt_5) a5,sum(tax_5) t5,sum(amt_8) a8,sum(tax_8) t8,sum(amt_10) a10,sum(tax_10) t10,sum(amt_z) az,sum(tax_z) tz from " + hd + " a,tk where a." + col +"=? and a.mst=tk.mst and a.khmshdon in (1,5) and a.tthai in (1,2,3,5) and a.ttxly in (5,6,8) group by a.mst)");
			sb.append(" select /*+ parallel(16) */ tin.*,da,a,da-a c7,dakct,akct,dakct-akct c10,da0,a0,da0-a0 c13,da5,a5,da5-a5 c16,da10,a10+a8 c18,da10-a10-a8 c19,dakkk,akkk,dakkk-akkk c22,az,dt,t,dt-t c26,dt5,t5,dt5-t5 c29,dt10,t10+t8 c31,dt10-t10-t8 c32,tz");
			sb.append(" from (select mst,sum(da) da,sum(dt) dt,sum(dakct) dakct,sum(dakkk) dakkk,sum(da0) da0,sum(da5) da5,sum(dt5) dt5,sum(da10) da10,sum(dt10) dt10,sum(a) a,sum(t) t,sum(akct) akct,sum(akkk) akkk,sum(a0) a0,sum(a5) a5,sum(t5) t5,sum(a8) a8,sum(t8) t8,sum(a10) a10,sum(t10) t10,sum(az) az,sum(tz) tz from");
			sb.append(" (select mst,0 da,0 dt,0 dakct,0 dakkk,0 da0,0 da5,0 dt5,0 da10,0 dt10,a,t,akct,akkk,a0,a5,t5,a8,t8,a10,t10,az,tz from hd union all select mst,da,dt,dakct,dakkk,da0,da5,dt5,da10,dt10,0 a,0 t,0 akct,0 akkk,0 a0,0 a5,0 t5,0 a8,0 t8,0 a10,0 t10,0 az,0 tz from tk) group by mst) x,tin where x.mst=tin.mst");
		} else {
			sb.append(",tk as (select tin.mst,sum(amt_buy) ba,sum(tax_buy) bt from tin," + tk + " where tin.mst=" + tk + ".mst and " + col + "=? group by tin.mst)");
			sb.append(",hd as (select a.mst,sum(amt) a,sum(tax) t from " + hd + " a,tk where a." + col + "=? and a.mst=tk.mst and a.khmshdon in (1,5) and a.tthai in (1,2,3,5) and a.ttxly in (5,6,8) group by a.mst)");
			sb.append(" select /*+ parallel(16) */ tin.*,ba,a,ba-a c7,bt,t,bt-t c10 from (select mst,sum(ba) ba,sum(bt) bt,sum(a) a,sum(t) t from");
			sb.append(" (select mst,0 ba,0 bt,a,t from hd union all select mst,ba,bt,0 a,0 t from tk) group by mst) x,tin where x.mst=tin.mst");
		}
		Object[] objs = param.toArray(new Object[0]);
		String sql = sb.toString();
		//log.info(sql);
		//log.info(Arrays.toString(objs));
		String tencha = ten_cqt_cha(cqt), tencqt = sys.get_ten_cqt(cqt), p4 = "BÁO CÁO ĐỐI CHIẾU CHI TIẾT TỜ KHAI 01/GTGT " + ("1".equals(ltk) ? "BÁN RA" : "MUA VÀO");
		// @formatter:on
		String[] header = { tencha.toUpperCase(), tencqt.toUpperCase(), p2, p3, p4 };
		r.setCode("r" + ltk);
		xls(r, sql, objs, header, 10);
	}

	private void r3_4(Report r) {
		// @formatter:off
		String cqt = r.getCqt(), fd = r.getFd(), mst = r.getMst(), m = Utils.yymm(fd), khmshdon = r.getKhmshdon(), code = r.getCode(), order;
		Integer fv = r.getFv(), tv = r.getTv();
		String p2, p3, p4;
		p3 = "Tháng: " + Utils.mmyyyy(fd);
		List<Object> param = new ArrayList<Object>();
		param.add(cqt);
		StringBuffer sb = new StringBuffer("with tin as (select tin mst,oname name from mv_tin where taxo=?");
		if (StringUtils.isNotBlank(mst)) {
			List<String> tins = new ArrayList<String>(Arrays.asList(mst.split(",")));
			sb.append(String.format(" and tin in (%s)", String.join(",", Collections.nCopies(tins.size(), "?"))));
			param.addAll(tins);
		}
		sb.append(")");
		if ("r3".equals(code)) {
			order = "amt";
			if (fv==null) fv = Integer.valueOf(0);
			p2 = "BÁO CÁO DOANH THU THEO NGƯỠNG";
			p4 = "Doanh thu từ "+ fv;
			param.add(m);
			param.add(khmshdon);
			param.add(fv.intValue() * 1000000000L);
			sb.append(",hd as (select /*+ parallel(16) */ tin.mst,tin.name,sum(a.amt) amt,sum(a.tax) tax FROM mv_sls_mm a,tin WHERE a.mm=? and a.mst=tin.mst and a.khmshdon=? AND a.tthai IN (1,2,3,5) AND a.ttxly IN (5,6,8) group by tin.mst,tin.name HAVING sum(a.amt)");
			if (tv==null) sb.append(">=?");
			else {
				sb.append(" between ? and ?");
				p4 = p4 + " đến " + tv + " tỷ đồng";
				param.add(tv.intValue() * 1000000000L);
			}	
		} else {
			order = "cnt";
			if (fv==null) fv = Integer.valueOf(1);
			p2 = "BÁO CÁO SỐ LƯỢNG HÓA ĐƠN HỦY THEO NGƯỠNG";
			p4 = "Số lượng hóa đơn hủy từ "+ fv;
			param.add(m);
			param.add(khmshdon);
			param.add(fv);
			String ltk = r.getLtk();
			String tab = "1".equals(ltk) ? "mv_sls_mm":"mv_buy_mm";
			sb.append(",hd as (select /*+ parallel(16) */ tin.mst,tin.name,sum(a.amt) amt,sum(a.tax) tax,sum(a.cnt) cnt FROM ");
			sb.append(tab);
			sb.append(" a,tin WHERE a.mm=? and a.mst=tin.mst and a.khmshdon=? AND a.tthai=6 AND a.ttxly IN (5,6,8) group by tin.mst,tin.name HAVING sum(a.cnt)");
			if (tv==null) sb.append(">=?");
			else {
				sb.append(" between ? and ?");
				p4 = p4 + " đến " + tv;
				param.add(tv);
			}	
		}
		sb.append(") select * from hd order by " + order + " desc");
		String sql = sb.toString();
		Object[] objs = param.toArray(new Object[0]);
		//log.info(sql);
		//log.info(Arrays.toString(objs));
		String tencha = ten_cqt_cha(cqt), tencqt = sys.get_ten_cqt(cqt);
		String[] header = { tencha.toUpperCase(), tencqt.toUpperCase(), p2, p3, p4 };
		xls(r, sql, objs, header, 6);
		// @formatter:on
	}

	private void r5(Report r) {
		// @formatter:off
		String ltk = r.getLtk(), lky = r.getLky(), fd = r.getFd(), cqt = r.getCqt(), mst = r.getMst();
		String p2, p3, f, t, c, k = lky.substring(0, 1), kk = k + k, mv = ("1".equals(ltk) ? "mv_sls_" : "mv_buy_") + kk;
		List<Object> param = new ArrayList<Object>();
		param.add(cqt);
		StringBuffer sb = new StringBuffer("with tin as (select tin mst,oname name from mv_tin where taxo=?");
		if (StringUtils.isNotBlank(mst)) {
			List<String> tins = new ArrayList<String>(Arrays.asList(mst.split(",")));
			sb.append(String.format(" and tin in (%s)", String.join(",", Collections.nCopies(tins.size(), "?"))));
			param.addAll(tins);
		}
		sb.append(")");
		switch (lky) {
		case "month":
			c = Utils.yymm(fd);
			f = Utils.prevm(fd);
			t = Utils.nextm(fd);
			p3 = "Tháng: " + Utils.mmyyyy(fd);
			sb.append(",hd1 as (select tin.mst,tin.name,mv.mm,sum(mv.amt) amt,sum(mv.tax) tax from ");
			sb.append(mv);
			sb.append(" mv,tin tin where mv.mm between ? and ? and mv.mst=tin.mst and mv.khmshdon in (1,5) and mv.tthai in (1,2,3,5) and mv.ttxly in (5,6,8) group by tin.mst,tin.name,mv.mm)");
			sb.append(",hd2 as (select mm,mst,name,amt,tax");
			sb.append(",lag(amt)  over (partition by mst order by mm) ap");
			sb.append(",lead(amt) over (partition by mst order by mm) an");
			sb.append(",lag(tax)  over (partition by mst order by mm) tp");
			sb.append(",lead(tax) over (partition by mst order by mm) tn");
			sb.append(" from hd1) select /*+ parallel(16) */ mst,name,ap,amt,an,tp,tax,tn from hd2 where mm=?");
			break;
		case "year":
			c = Utils.yy(fd);
			f = Utils.prevy(fd);
			t = Utils.nexty(fd);
			p3 = "Năm: " + Utils.yyyy(fd);
			sb.append(",hd1 as (select tin.mst,tin.name,mv.yy,sum(mv.amt) amt,sum(mv.tax) tax from ");
			sb.append(mv);
			sb.append(" mv,tin tin where mv.yy between ? and ? and mv.mst=tin.mst and mv.khmshdon in (1,5) and mv.tthai in (1,2,3,5) and mv.ttxly in (5,6,8) group by tin.mst,tin.name,mv.yy)");
			sb.append(",hd2 as (select yy,mst,name,amt,tax");
			sb.append(",lag(amt)  over (partition by mst order by yy) ap");
			sb.append(",lead(amt) over (partition by mst order by yy) an");
			sb.append(",lag(tax)  over (partition by mst order by yy) tp");
			sb.append(",lead(tax) over (partition by mst order by yy) tn");
			sb.append(" from hd1) select /*+ parallel(16) */ mst,name,ap,amt,an,tp,tax,tn from hd2 where yy=?");
			break;
		default:
			c = Utils.yyq(fd);
			f = Utils.prevq(fd);
			t = Utils.nextq(fd);
			p3 = "Quý: " + Utils.qqyyyy(fd);
			sb.append(",hd1 as (select tin.mst,tin.name,mv.qq,sum(mv.amt) amt,sum(mv.tax) tax from ");
			sb.append(mv);
			sb.append(" mv,tin tin where mv.qq between ? and ? and mv.mst=tin.mst and mv.khmshdon in (1,5) and mv.tthai in (1,2,3,5) and mv.ttxly in (5,6,8) group by tin.mst,tin.name,mv.qq)");
			sb.append(",hd2 as (select qq,mst,name,amt,tax");
			sb.append(",lag(amt)  over (partition by mst order by qq) ap");
			sb.append(",lead(amt) over (partition by mst order by qq) an");
			sb.append(",lag(tax)  over (partition by mst order by qq) tp");
			sb.append(",lead(tax) over (partition by mst order by qq) tn");
			sb.append(" from hd1) select /*+ parallel(16) */ mst,name,ap,amt,an,tp,tax,tn from hd2 where qq=?");
			break;
		}
		param.add(f);
		param.add(t);
		param.add(c);
		Object[] objs = param.toArray(new Object[0]);
		String sql = sb.toString();
		//log.info(sql);
		//log.info(Arrays.toString(objs));
		String tencha = ten_cqt_cha(cqt), tencqt = sys.get_ten_cqt(cqt);
		p2 = "BÁO CÁO SO SÁNH HÓA ĐƠN " + ("2".equals(ltk) ? "MUA VÀO" : "BÁN RA");
		String[] header = { tencha.toUpperCase(), tencqt.toUpperCase(), p2, p3 };
		xls(r, sql, objs, header, 7);
		// @formatter:on
	}

	private void r6(Report r) {
		// @formatter:off
		String ltk = r.getLtk(), lky = r.getLky(), lsl = r.getLsl(), fd = r.getFd(), cqt = r.getCqt(), mst = r.getMst();
		String c, p2, p3, k = lky.substring(0, 1), kk = k + k, mv = ("1".equals(ltk) ? "mv_sls_" : "mv_buy_") + kk,	cd = "1".equals(lsl) ? "amt" : "tax";
		List<Object> param = new ArrayList<Object>();
		param.add(cqt);
		StringBuffer sb = new StringBuffer("with tin as (select tin mst,oname name from mv_tin where taxo=?");
		if (StringUtils.isNotBlank(mst)) {
			List<String> tins = new ArrayList<String>(Arrays.asList(mst.split(",")));
			sb.append(String.format(" and tin in (%s)", String.join(",", Collections.nCopies(tins.size(), "?"))));
			param.addAll(tins);
		}
		sb.append("),hd1 as (select tin.mst,tin.name,sum(mv.amt) amt,sum(mv.tax) tax from ");
		sb.append(mv);
		sb.append(" mv,tin tin where mv.");
		sb.append(kk);
		sb.append("=? and mv.mst=tin.mst and mv.khmshdon in (1,5) and mv.tthai in (1,2,3,5) and mv.ttxly in (5,6,8) group by tin.mst,tin.name)");
		sb.append(" select /*+ parallel(16) */ mst,name,amt,tax,round(cume_dist() over (order by ");
		sb.append(cd);
		sb.append(" desc)*100,2) cd from hd1");
		switch (lky) {
		case "month":
			c = Utils.yymm(fd);
			p3 = "Tháng: " + Utils.mmyyyy(fd);
			break;
		case "year":
			c = Utils.yy(fd);
			p3 = "Năm: " + Utils.yyyy(fd);
			break;
		default:
			c = Utils.yyq(fd);
			p3 = "Quý: " + Utils.qqyyyy(fd);
			break;
		}
		param.add(c);
		Object[] objs = param.toArray(new Object[0]);
		String sql = sb.toString();
		//log.info(sql);
		//log.info(Arrays.toString(objs));
		String tencha = ten_cqt_cha(cqt), tencqt = sys.get_ten_cqt(cqt);
		p2 = "BÁO CÁO PHÂN PHỐI TÍCH LŨY " + ("2".equals(lsl) ? "SỐ THUẾ" : "DOANH SỐ") + " CỦA HÓA ĐƠN " + ("2".equals(ltk) ? "MUA VÀO" : "BÁN RA");
		String[] header = { tencha.toUpperCase(), tencqt.toUpperCase(), p2, p3 };
		xls(r, sql, objs, header, 6);
		// @formatter:on
	}

	private void r7(Report r) {
		// @formatter:off
		String ltk = r.getLtk(), lky = r.getLky(), lsl = r.getLsl(), fd = r.getFd(), cqt = r.getCqt(), mst = r.getMst();
		String c, p2, p3, k = lky.substring(0, 1), kk = k + k, mv = ("1".equals(ltk) ? "mv_sls_" : "mv_buy_") + kk,	cd = "1".equals(lsl) ? "amt" : "tax";
		Integer rn = r.getRn();
		List<Object> param = new ArrayList<Object>();
		param.add(cqt);
		StringBuffer sb = new StringBuffer("with tin as (select tin mst,oname name from mv_tin where taxo=?");
		if (StringUtils.isNotBlank(mst)) {
			List<String> tins = new ArrayList<String>(Arrays.asList(mst.split(",")));
			sb.append(String.format(" and tin in (%s)", String.join(",", Collections.nCopies(tins.size(), "?"))));
			param.addAll(tins);
		}
		sb.append("),hd1 as (select tin.mst,tin.name,sum(mv.amt) amt,sum(mv.tax) tax from ");
		sb.append(mv);
		sb.append(" mv,tin tin where mv.");
		sb.append(kk);
		sb.append("=? and mv.mst=tin.mst and mv.khmshdon in (1,5) and mv.tthai in (1,2,3,5) and mv.ttxly in (5,6,8) group by tin.mst,tin.name)");
		sb.append(",hd2 as (select /*+ parallel(16) */ mst,name,amt,tax,DENSE_RANK() over (order by ");
		sb.append(cd);
		sb.append(" desc) rn from hd1) select * from hd2 where rn<=?");
		switch (lky) {
		case "month":
			c = Utils.yymm(fd);
			p3 = "Tháng: " + Utils.mmyyyy(fd);
			break;
		case "year":
			c = Utils.yy(fd);
			p3 = "Năm: " + Utils.yyyy(fd);
			break;
		default:
			c = Utils.yyq(fd);
			p3 = "Quý: " + Utils.qqyyyy(fd);
			break;
		}
		param.add(c);
		param.add(rn);
		Object[] objs = param.toArray(new Object[0]);
		String sql = sb.toString();
		//log.info(sql);
		//log.info(Arrays.toString(objs));
		String tencha = ten_cqt_cha(cqt), tencqt = sys.get_ten_cqt(cqt);
		p2 = "BÁO CÁO PHÂN HẠNG THEO " + ("2".equals(lsl) ? "SỐ THUẾ" : "DOANH SỐ") + " CỦA HÓA ĐƠN " + ("2".equals(ltk) ? "MUA VÀO" : "BÁN RA");					
		String[] header = { tencha.toUpperCase(), tencqt.toUpperCase(), p2, p3 };
		xls(r, sql, objs, header, 6);
		// @formatter:on
	}

	private void r8(Report r) {
		String fd = r.getFd(), cqt = r.getCqt(), lky = r.getLky(), ltk = r.getLtk(), lsl = r.getLsl(), pk, col, tk, hd,
				cd = "1".equals(lsl) ? "amt" : "tax";
		Integer title = r.getNtitle();
		int ntitle = (title == null || title == 0) ? 4 : title.intValue();
		StringJoiner sjc = new StringJoiner(",");
		StringJoiner sjv = new StringJoiner(",");
		StringJoiner sjn = new StringJoiner(",");
		for (int i = 1; i <= ntitle; i++) {
			String c = " c" + i;
			sjc.add(i + c);
			sjv.add("to_char(" + c + "_v)" + c + "_v");
			sjn.add("'Ngưỡng " + i + " từ '||" + c + "_v");
		}
		String jc = sjc.toString(), jv = sjv.toString(), jn = sjn.toString(), p2, p3;
		if ("month".equals(lky)) {
			pk = Utils.yymm(fd);
			col = "mm";
			tk = "mv_tk_mm";
			hd = "1".equals(ltk) ? "mv_sls_mm" : "mv_buy_mm";
			p3 = "Tháng: " + Utils.mmyyyy(fd);
		} else {
			pk = Utils.yyq(fd);
			col = "qq";
			tk = "mv_tk_qq";
			hd = "1".equals(ltk) ? "mv_sls_qq" : "mv_buy_qq";
			p3 = "Quý: " + Utils.qqyyyy(fd);
		}
		List<Object> param = new ArrayList<Object>();
		param.add(cqt);
		param.add(cqt);
		param.add(pk);
		param.add(pk);
		// @formatter:off
		StringBuffer sb = new StringBuffer("with tax as (select ma_cqt,ten_cqt_dai from ztb_map_cqt where MANDT='500' and (ma_cqt=? or ma_cha_4=?)),tin as (select a.tin mst,b.ten_cqt_dai cqt from mv_tin a,");
		if ("0000".equals(cqt))
			sb.append("(select x.ma_cqt,y.ten_cqt_dai from ztb_map_cqt x,tax y where x.MANDT='500' and (x.ma_cqt=y.ma_cqt or x.ma_cha_4=y.ma_cqt)) b");
		else
			sb.append("tax b");
		sb.append(" where a.taxo=b.ma_cqt)");
		if ("1".equals(ltk)) 
			sb.append(",tk as (select tin.mst,sum(" + cd + ") val from tin," + tk + " where tin.mst=" + tk + ".mst and " + col + "=? group by tin.mst)");
		else 
			sb.append(",tk as (select tin.mst,sum(" + cd + "_buy) val from tin," + tk + " where tin.mst=" + tk + ".mst and " + col + "=? group by tin.mst)");
		sb.append(",hd as (select a.mst,sum(a." + cd + ") val from tk," + hd + " a where a." + col + "=? and a.mst=tk.mst and a.khmshdon in (1,5) and a.tthai in (1,2,3,5) and a.ttxly in (5,6,8) group by a.mst)");
		sb.append(",t1 as (select mst,v,ntile(" + ntitle + ") over(order by v) grp from (select mst,round(sum(abs(k-h))/1000000,2) v from (select mst,val k,0 h from tk union all select mst,0 k,val h from hd) group by mst))");
		sb.append(",t3 as (select * from (select grp,'Tên Cơ quan thuế' cqt,round(min(v)) v1,round(max(v)) v2 from t1 group by grp) pivot(max(v1||' đến '||v2) v for grp in (" + jc + ")))");
		sb.append(",t5 as (select * from (select t1.grp,tin.cqt,t1.mst from t1,tin where t1.mst=tin.mst) pivot(count(mst) v for grp in (" + jc + "))) select * from (select /*+ parallel(16) */ cqt," + jn + " from t3 union select cqt," + jv + " from t5) order by cqt desc");
		String sql = sb.toString();
		Object[] objs = param.toArray(new Object[0]);
		//log.info(sql);
		//log.info(Arrays.toString(objs));
		String tencha = ten_cqt_cha(cqt), tencqt = sys.get_ten_cqt(cqt);
		p2 = "BÁO CÁO ĐỐI CHIẾU TỔNG HỢP "+ ("2".equals(lsl) ? "SỐ THUẾ " : "DOANH SỐ ") +" TỜ KHAI 01/GTGT "+ ("2".equals(ltk) ? "MUA VÀO" : "BÁN RA");
		String[] header = { tencha.toUpperCase(), tencqt.toUpperCase(), p2, p3 };
		xls(r, sql, objs, header, 4);
		// @formatter:on
	}

	private void r10(Report r) {
		// @formatter:off
		String fd = r.getFd(), cqt = r.getCqt(), tthai = r.getTthai(), lky = r.getLky(), mst = r.getMst(), khmshdon = r.getKhmshdon(), loaidl = r.getLoaidl(), ltk=r.getLtk();
		List<Object> param = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer("select /*+ parallel(16) */ khmshdon,khhdon,shdon,nbmst,nbten,nmmst,nmten,to_char(tdlap,'dd/mm/yyyy') tdlap,tgtcthue,tgtthue,tgtttbso,tthai,trunc(tdlap-ngay_htoan) gap,khmshdgoc,khhdgoc,shdgoc,nmmstgoc,nmtengoc,to_char(tdlapgoc,'dd/mm/yyyy') tdlapgoc,tgtcthuegoc,tgtthuegoc,tgtttbsogoc,tthaigoc from mv_r10");
		if ("1".equals(ltk)) {	
			param.add(Utils.yymm(fd));
			param.add(cqt);
			param.add(tthai);
			param.add(khmshdon);
			param.add(loaidl);
			sb.append(" where mm=? and cqt=? and tthai=? and khmshdon=? and loai_dl=?");
			if (StringUtils.isNotBlank(mst)) {
				List<String> tins = new ArrayList<String>(Arrays.asList(mst.split(",")));
				sb.append(String.format(" and nbmst in (%s)", String.join(",", Collections.nCopies(tins.size(), "?"))));
				param.addAll(tins);
			}
			sb.append(" and mm");
			sb.append("1".equals(lky) ? "=" : "<>");
			sb.append("ht order by tdlap");
		}
		else {
			param.add(cqt);			
			param.add(Utils.yymm(fd));
			param.add(tthai);
			param.add(khmshdon);
			param.add(loaidl);
			sb.append(" a,(select tin from mv_tin where taxo=?) b where a.mm=? and a.tthai=? and a.khmshdon=? and a.loai_dl=? and a.nmmst=b.tin");
			if (StringUtils.isNotBlank(mst)) {
				List<String> tins = new ArrayList<String>(Arrays.asList(mst.split(",")));
				sb.append(String.format(" and a.nmmst in (%s)", String.join(",", Collections.nCopies(tins.size(), "?"))));
				param.addAll(tins);
			}
			sb.append(" and a.mm");
			sb.append("1".equals(lky) ? "=" : "<>");
			sb.append("a.ht order by a.tdlap");
		}	
		Object[] objs = param.toArray(new Object[0]);
		String sql = sb.toString();
		//log.info(sql);
		//log.info(Arrays.toString(objs));
		csv(r, sql, objs);
		// @formatter:on
	}

	private void r11(Report r) {
		// @formatter:off
		String fd = r.getFd(), cqt = r.getCqt(), mst = r.getMst(), khmshdon = r.getKhmshdon(), loaidl = r.getLoaidl(), ltk=r.getLtk();
		StringBuffer sb = new StringBuffer("select /*+ parallel(16) */ khmshdon,khhdon,shdon,to_char(tdlap,'dd/mm/yyyy') tdlap,nbmst,nbten,nmmst,nmten,tgtcthue,tgtthue,tgtttbso,dvtte,tgia,tthai from mv_r11");
		List<Object> param = new ArrayList<Object>();
		if ("1".equals(ltk)) {
			param.add(Utils.yymm(fd));
			param.add(cqt);
			param.add(khmshdon);
			param.add(loaidl);
			sb.append(" where mm=? and cqt=? and khmshdon=? and loai_dl=?");
			if (StringUtils.isNotBlank(mst)) {
				List<String> tins = new ArrayList<String>(Arrays.asList(mst.split(",")));
				sb.append(String.format(" and nbmst in (%s)", String.join(",", Collections.nCopies(tins.size(), "?"))));
				param.addAll(tins);
			}
		}
		else {
			param.add(cqt);
			param.add(Utils.yymm(fd));
			param.add(khmshdon);
			param.add(loaidl);
			sb.append(" a,(select tin from mv_tin where taxo=?) b where a.mm=? and a.khmshdon=? and a.loai_dl=? and a.nmmst=b.tin");
			if (StringUtils.isNotBlank(mst)) {
				List<String> tins = new ArrayList<String>(Arrays.asList(mst.split(",")));
				sb.append(String.format(" and a.nmmst in (%s)", String.join(",", Collections.nCopies(tins.size(), "?"))));
				param.addAll(tins);
			}
		}
		Object[] objs = param.toArray(new Object[0]);
		String sql = sb.toString();
		//log.info(sql);
		//log.info(Arrays.toString(objs));
		csv(r, sql, objs);
		// @formatter:on
	}

	private void r12(Report r) {
		// @formatter:off
		String khmshdon = r.getKhmshdon(), lky = r.getLky(), fd = r.getFd(), cqt = r.getCqt(), mst = r.getMst();
		String p2, p3, c, k = lky.substring(0, 1), kk = k + k;
		switch (lky) {
		case "month":
			c = Utils.yymm(fd);
			p3 = "Tháng: " + Utils.mmyyyy(fd);
			break;
		case "year":
			c = Utils.yy(fd);
			p3 = "Năm: " + Utils.yyyy(fd);
			break;
		default:
			c = Utils.yyq(fd);
			p3 = "Quý: " + Utils.qqyyyy(fd);
			break;
		}
		List<Object> param = new ArrayList<Object>();
		param.add(cqt);
		StringBuffer sb = new StringBuffer("with tin as (select tin mst,oname name from mv_tin where taxo=?");
		if (StringUtils.isNotBlank(mst)) {
			List<String> tins = new ArrayList<String>(Arrays.asList(mst.split(",")));
			sb.append(String.format(" and tin in (%s)", String.join(",", Collections.nCopies(tins.size(), "?"))));
			param.addAll(tins);
		}
		sb.append(")");
		param.add(c);
		param.add(khmshdon);
		param.add(c);
		param.add(khmshdon);
		String wh=" a,tin where a." + kk + "=? and a.mst=tin.mst and a.khmshdon=? and a.tthai in (1,2,3,5) and a.ttxly in (5,6,8) group by tin.mst,tin.name)";
		sb.append(",hd1 as (select tin.mst,tin.name,sum(a.amt) a1,sum(a.tax) t1,0 a2,0 t2 from mv_sls_" + kk + wh);
		sb.append(",hd2 as (select tin.mst,tin.name,0 a1,0 t1,sum(a.amt) a2,sum(a.tax) t2 from mv_buy_" + kk + wh);
		sb.append(",hd3 as (select mst,name,sum(a2) a2,sum(a1) a1,sum(t2) t2,sum(t1) t1 from (select * from hd1 union all select * from hd2) group by mst,name)");
		sb.append(",hd4 as (select mst,name,a2,a1,t2,t1,a1-a2 a3 from hd3) select /*+ parallel(16) */ mst,name,a2,a1,t2,t1,a3,case a2 when 0 then to_number(null) else abs(a3)/a2 end a4 from hd4 order by a3");
		Object[] objs = param.toArray(new Object[0]);
		String sql = sb.toString();
		//log.info(sql);
		//log.info(Arrays.toString(objs));
		String tencha = ten_cqt_cha(cqt), tencqt = sys.get_ten_cqt(cqt);
		p2 = "BÁO CÁO SO SÁNH HÓA ĐƠN MUA VÀO BÁN RA";
		String[] header = { tencha.toUpperCase(), tencqt.toUpperCase(), p2 ,p3 };
		xls(r, sql, objs, header, 6);
		// @formatter:on
	}

	private void r13(Report r) {
		String fd = r.getFd(), cqt = r.getCqt(), mst = r.getMst(), ltk = r.getLtk(), lky = r.getLky(), tk, hd, col, p3,
				pk;
		if ("month".equals(lky)) {
			pk = Utils.yymm(fd);
			col = "mm";
			tk = "mv_tk_mm";
			hd = "1".equals(ltk) ? "mv_sls_mm" : "mv_buy_mm";
			p3 = "Tháng: " + Utils.mmyyyy(fd);
		} else {
			pk = Utils.yyq(fd);
			col = "qq";
			tk = "mv_tk_qq";
			hd = "1".equals(ltk) ? "mv_sls_qq" : "mv_buy_qq";
			p3 = "Quý: " + Utils.qqyyyy(fd);
		}
		List<Object> param = new ArrayList<Object>();
		param.add(cqt);
		StringBuffer sb = new StringBuffer("with tin as (select tin mst,oname name from mv_tin where taxo=?");
		if (StringUtils.isNotBlank(mst)) {
			List<String> tins = new ArrayList<String>(Arrays.asList(mst.split(",")));
			sb.append(String.format(" and tin in (%s)", String.join(",", Collections.nCopies(tins.size(), "?"))));
			param.addAll(tins);
		}
		sb.append(")");
		param.add(pk);
		param.add(pk);
		// @formatter:off
		sb.append(",t1 as (select /*+ parallel(16) */ tin.*,sum(a.amt) a,sum(a.tax) t from tin," + hd + " a where a."+ col + "=? and a.mst=tin.mst and a.khmshdon in (1,5) and a.tthai in (1,2,3,5) and a.ttxly in (5,6,8) and not exists (select 1 from " + tk + " where " + col + "=? and mst=a.mst) group by tin.mst,tin.name) select * from t1 order by a");
		// @formatter:on
		Object[] objs = param.toArray(new Object[0]);
		String sql = sb.toString();
		// log.info(sql);
		// log.info(Arrays.toString(objs));
		String tencha = ten_cqt_cha(cqt), tencqt = sys.get_ten_cqt(cqt);
		String p2 = "BÁO CÁO NNT PHÁT SINH HÓA ĐƠN " + ("2".equals(ltk) ? "MUA VÀO" : "BÁN RA") + " CHƯA CÓ TỜ KHAI";
		String[] header = { tencha.toUpperCase(), tencqt.toUpperCase(), p2, p3 };
		xls(r, sql, objs, header, 6);
	}

	private void r14(Report r) {
		String fd = r.getFd(), cqt = r.getCqt(), mst = r.getMst(), lky = r.getLky(), khmshdon = r.getKhmshdon();
		String hd1, hd2, pk, col, p2 = "BÁO CÁO GIÁM SÁT TÌNH HÌNH SỬ DỤNG HÓA ĐƠN", p3;
		switch (lky) {
		case "month":
			p3 = "Tháng: " + Utils.mmyyyy(fd);
			hd1 = "mv_sls_mm";
			hd2 = "mv_buy_mm";
			pk = Utils.yymm(fd);
			col = "mm";
			break;
		case "year":
			p3 = "Năm: " + Utils.yyyy(fd);
			hd1 = "mv_sls_yy";
			hd2 = "mv_buy_yy";
			pk = Utils.yyyy(fd);
			col = "yy";
			break;
		default:
			p3 = "Quý: " + Utils.qqyyyy(fd);
			hd1 = "mv_sls_qq";
			hd2 = "mv_buy_qq";
			pk = Utils.yyq(fd);
			col = "qq";
			break;
		}
		List<Object> param = new ArrayList<Object>();
		param.add(cqt);
		StringBuffer sb = new StringBuffer(
				"with tin as (select a.tin mst,a.oname name,c.ten_nnkd,a.vncap,a.vncapw,a.nncap,a.nncapw,to_char(a.found_dat,'dd/mm/yyyy') found_dat,b.c1,b.c2 from mv_tin a,mv_owner b,dm_nganhktqd c where a.taxo=? and a.ind_sector=c.ma_nnkd and a.tin=b.tin");
		if (StringUtils.isNotBlank(mst)) {
			List<String> tins = new ArrayList<String>(Arrays.asList(mst.split(",")));
			sb.append(String.format(" and a.tin in (%s)", String.join(",", Collections.nCopies(tins.size(), "?"))));
			param.addAll(tins);
		}
		sb.append(")");
		// @formatter:off
		sb.append(",t1 as (select tin.mst,sum(case when a.tthai in (1,2,3,5) then a.cnt else 0 end) sc,sum(case when a.tthai in (4,6) then a.cnt else 0 end) s46,sum(case when a.tthai in (1,2,3,5) then a.amt else 0 end) sa,sum(case when a.tthai in (1,2,3,5) then a.tax else 0 end) st from tin," + hd1 + " a where a." + col +"=? and a.mst=tin.mst and a.khmshdon=? and a.ttxly in (5,6,8) group by tin.mst)");
		sb.append(",t2 as (select tin.mst,sum(case when a.tthai in (1,2,3,5) then a.cnt else 0 end) bc,sum(case when a.tthai in (4,6) then a.cnt else 0 end) b46,sum(case when a.tthai in (1,2,3,5) then a.amt else 0 end) ba,sum(case when a.tthai in (1,2,3,5) then a.tax else 0 end) bt from tin," + hd2 + " a where a." + col +"=? and a.mst=tin.mst and a.khmshdon=? and a.ttxly in (5,6,8) group by tin.mst)");
		sb.append(",t3 as (select mst,sc,s46,sa,st,0 bc,0 b46,0 ba,0 bt from t1 union all select mst,0 sc,0 s46,0 sa,0 st,bc,b46,ba,bt from t2)");
		sb.append(",t4 as (select mst,sum(sc) sc,sum(s46) s46,sum(sa) sa,sum(st) st,sum(bc) bc,sum(b46) b46,sum(ba) ba,sum(bt) bt from t3 group by mst)");
		sb.append("select /*+ parallel(16) */ tin.*,t4.sc,t4.s46,t4.sa,t4.st,t4.bc,t4.b46,t4.ba,t4.bt from tin,t4 where tin.mst=t4.mst");
		// @formatter:on
		param.add(pk);
		param.add(khmshdon);
		param.add(pk);
		param.add(khmshdon);
		Object[] objs = param.toArray(new Object[0]);
		String sql = sb.toString();
		// log.info(sql);
		// log.info(Arrays.toString(objs));
		String tencha = ten_cqt_cha(cqt), tencqt = sys.get_ten_cqt(cqt);
		String[] header = { tencha.toUpperCase(), tencqt.toUpperCase(), p2, p3 };
		xls(r, sql, objs, header, 7);
	}

	private void r15(Report r) {
		String fd = r.getFd(), cqt = r.getCqt(), mst = r.getMst(), lky = r.getLky(), khmshdon = r.getKhmshdon();
		String p2 = "BÁO CÁO ĐÁNH GIÁ TÌNH HÌNH SỬ DỤNG HÓA ĐƠN", p3, hd1, hd2, mx, pk, col;
		switch (lky) {
		case "month":
			mx = Utils.prevm(fd);
			pk = Utils.yymm(fd);
			p3 = "Tháng: " + Utils.mmyyyy(fd);
			hd1 = "mv_sls_mm";
			hd2 = "mv_buy_mm";
			col = "mm";
			break;
		case "year":
			mx = Utils.prevy(fd);
			pk = Utils.yyyy(fd);
			p3 = "Năm: " + Utils.yyyy(fd);
			hd1 = "mv_sls_yy";
			hd2 = "mv_buy_yy";
			col = "yy";
			break;
		default:
			mx = Utils.prevq(fd);
			pk = Utils.yyq(fd);
			p3 = "Quý: " + Utils.qqyyyy(fd);
			hd1 = "mv_sls_qq";
			hd2 = "mv_buy_qq";
			col = "qq";
			break;
		}
		List<Object> param = new ArrayList<Object>();
		param.add(cqt);

		// @formatter:off
		StringBuffer sb = new StringBuffer("with tin as (select a.tin mst,a.oname name,c.ten_nnkd,a.vncap,a.vncapw,a.nncap,a.nncapw,to_char(a.found_dat,'dd/mm/yyyy') found_dat,b.c1,b.c2 from mv_tin a,mv_owner b,dm_nganhktqd c where a.taxo=? and a.ind_sector=c.ma_nnkd and a.tin=b.tin");
		if (StringUtils.isNotBlank(mst)) {
			List<String> tins = new ArrayList<String>(Arrays.asList(mst.split(",")));
			sb.append(String.format(" and a.tin in (%s)", String.join(",", Collections.nCopies(tins.size(), "?"))));
			param.addAll(tins);
		}
		sb.append(")");
		sb.append(",t0 as (select tin.mst,sum(case when a.tthai in (1,2,3,5) then a.cnt else 0 end) c1,sum(case when a.tthai in (4,6) then a.cnt else 0 end) c2,sum(case when a.tthai in (1,2,3,5) then a.amt else 0 end) c3,sum(case when a.tthai in (1,2,3,5) then a.tax else 0 end) c4 from tin," + hd1 + " a where a." + col +"=? and a.mst=tin.mst and a.khmshdon=? and a.ttxly in (5,6,8) group by tin.mst)");
		sb.append(",t1 as (select tin.mst,sum(case when a.tthai in (1,2,3,5) then a.cnt else 0 end) c5,sum(case when a.tthai in (4,6) then a.cnt else 0 end) c6,sum(case when a.tthai in (1,2,3,5) then a.amt else 0 end) c7,sum(case when a.tthai in (1,2,3,5) then a.tax else 0 end) c8 from tin," + hd1 + " a where a." + col +"=? and a.mst=tin.mst and a.khmshdon=? and a.ttxly in (5,6,8) group by tin.mst)");
		sb.append(",t2 as (select tin.mst,sum(case when a.tthai in (1,2,3,5) then a.cnt else 0 end) c9,sum(case when a.tthai in (4,6) then a.cnt else 0 end) c10,sum(case when a.tthai in (1,2,3,5) then a.amt else 0 end) c11,sum(case when a.tthai in (1,2,3,5) then a.tax else 0 end) c12 from tin," + hd2 + " a where a." + col +"=? and a.mst=tin.mst and a.khmshdon=? and a.ttxly in (5,6,8) group by tin.mst)");
		sb.append(",t3 as (select tin.mst,sum(case when a.tthai in (1,2,3,5) then a.cnt else 0 end) c13,sum(case when a.tthai in (4,6) then a.cnt else 0 end) c14,sum(case when a.tthai in (1,2,3,5) then a.amt else 0 end) c15,sum(case when a.tthai in (1,2,3,5) then a.tax else 0 end) c16 from tin," + hd2 + " a where a." + col +"=? and a.mst=tin.mst and a.khmshdon=? and a.ttxly in (5,6,8) group by tin.mst)");
		sb.append(",t4 as (select mst,c1,c2,c3,c4,0 c5,0 c6,0 c7,0 c8,0 c9,0 c10,0 c11,0 c12,0 c13,0 c14,0 c15,0 c16 from t0 union all select mst,0 c1,0 c2,0 c3,0 c4,c5,c6,c7,c8,0 c9,0 c10,0 c11,0 c12,0 c13,0 c14,0 c15,0 c16 from t1 union all select mst,0 c1,0 c2,0 c3,0 c4,0 c5,0 c6,0 c7,0 c8,c9,c10,c11,c12,0 c13,0 c14,0 c15,0 c16 from t2 union all select mst,0 c1,0 c2,0 c3,0 c4,0 c5,0 c6,0 c7,0 c8,0 c9,0 c10,0 c11,0 c12,c13,c14,c15,c16 from t3)");
		sb.append(",t5 as (select mst,sum(c1) c01,sum(c2) c02,sum(c3) c03,sum(c4) c04,sum(c5) c05,sum(c6) c06,sum(c7) c07,sum(c8) c08,sum(c9) c09,sum(c10) c10,sum(c11) c11,sum(c12) c12,sum(c13) c13,sum(c14) c14,sum(c15) c15,sum(c16) c16 from t4 group by mst)");
		sb.append("select /*+ parallel(16) */ tin.*,c01,c05,c02,c06,c03,c07,c04,c08,c09,c13,c10,c14,c11,c15,c12,c16 from tin,t5 where tin.mst=t5.mst");
		// @formatter:on
		param.add(mx);
		param.add(khmshdon);
		param.add(pk);
		param.add(khmshdon);
		param.add(mx);
		param.add(khmshdon);
		param.add(pk);
		param.add(khmshdon);
		Object[] objs = param.toArray(new Object[0]);
		String sql = sb.toString();
		// log.info(sql);
		// log.info(Arrays.toString(objs));
		String tencha = ten_cqt_cha(cqt), tencqt = sys.get_ten_cqt(cqt);
		String[] header = { tencha.toUpperCase(), tencqt.toUpperCase(), p2, p3 };
		xls(r, sql, objs, header, 7);
	}

	private void r16(Report r) {
		// @formatter:off
		String fd = r.getFd(), cqt = r.getCqt(), mst = r.getMst(), khmshdon = r.getKhmshdon(), loaidl = r.getLoaidl(), ltk = r.getLtk();
		List<Object> param = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer("select /*+ parallel(16) */ khmshdon,khhdon,shdon,to_char(tdlap,'dd/mm/yyyy') tdlap,nbmst,nbten,nmmst,nmten,tgtcthue,tgtthue,tgtttbso from mv_r16");
		if ("1".equals(ltk)) {
			param.add(Utils.yymm(fd));
			param.add(cqt);
			param.add(khmshdon);
			param.add(loaidl);
			sb.append(" where mm=? and cqt=? and khmshdon=? and loai_dl=?");
			if (StringUtils.isNotBlank(mst)) {
				List<String> tins = new ArrayList<String>(Arrays.asList(mst.split(",")));
				sb.append(String.format(" and nbmst in (%s)", String.join(",", Collections.nCopies(tins.size(), "?"))));
				param.addAll(tins);
			}

		} else {
			param.add(cqt);
			param.add(Utils.yymm(fd));
			param.add(khmshdon);
			param.add(loaidl);
			sb.append(" a,(select tin from mv_tin where taxo=?) b where a.mm=? and a.khmshdon=? and a.loai_dl=? and a.nmmst=b.tin");
			if (StringUtils.isNotBlank(mst)) {
				List<String> tins = new ArrayList<String>(Arrays.asList(mst.split(",")));
				sb.append(String.format(" and a.nmmst in (%s)", String.join(",", Collections.nCopies(tins.size(), "?"))));
				param.addAll(tins);
			}
		}
		Object[] objs = param.toArray(new Object[0]);
		String sql = sb.toString();
		//log.info(sql);
		//log.info(Arrays.toString(objs));
		csv(r, sql, objs);
		// @formatter:on
	}

	private void r17(Report r) {
		// @formatter:off
		String fd = r.getFd(), cqt = r.getCqt(), mst = r.getMst(), khmshdon = r.getKhmshdon(), loaidl = r.getLoaidl(), ltk = r.getLtk();
		List<Object> param = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer("select /*+ parallel(16) */ to_char(tdlap,'dd/mm/yyyy') tdlap,khmshdon,khhdon,shdon,nbmst,nbten,nmmst,nmten,tgtcthue,tgtthue,tgtttbso,tthai,dvtte,tgia,khmshdgoc,khhdgoc,shdgoc,tdlhdgoc from mv_r17");
		if ("1".equals(ltk)) {
			param.add(Utils.yymm(fd));
			param.add(cqt);
			param.add(khmshdon);
			param.add(loaidl);
			sb.append(" where mm=? and cqt=? and khmshdon=? and loai_dl=?");
			if (StringUtils.isNotBlank(mst)) {
				List<String> tins = new ArrayList<String>(Arrays.asList(mst.split(",")));
				sb.append(String.format(" and nbmst in (%s)", String.join(",", Collections.nCopies(tins.size(), "?"))));
				param.addAll(tins);
			}
		}
		else {
			param.add(cqt);
			param.add(Utils.yymm(fd));
			param.add(khmshdon);
			param.add(loaidl);
			sb.append(" a,(select tin from mv_tin where taxo=?) b where a.mm=? and a.khmshdon=? and a.loai_dl=? and a.nmmst=b.tin");
			if (StringUtils.isNotBlank(mst)) {
				List<String> tins = new ArrayList<String>(Arrays.asList(mst.split(",")));
				sb.append(String.format(" and a.nmmst in (%s)", String.join(",", Collections.nCopies(tins.size(), "?"))));
				param.addAll(tins);
			}
		}
		Object[] objs = param.toArray(new Object[0]);
		String sql = sb.toString();
		//log.info(sql);
		//log.info(Arrays.toString(objs));
		csv(r, sql, objs);
		// @formatter:on
	}

	private void r18(Report r) {
		String fd = r.getFd(), cqt = r.getCqt(), lbc = r.getLbc(), mst = r.getMst(), lky = r.getLky(), ltk = r.getLtk(),
				pk, col, tk, hd, tin, p2, p3, p4, bcode, bname, btab, acode, ten = "Tất cả";
		if ("month".equals(lky)) {
			pk = Utils.yymm(fd);
			col = "mm";
			tk = "mv_tkz_mm";
			hd = "mv_sls_mm";
			p3 = "Tháng: " + Utils.mmyyyy(fd);
		} else {
			pk = Utils.yyq(fd);
			col = "qq";
			tk = "mv_tkz_qq";
			hd = "mv_sls_qq";
			p3 = "Quý: " + Utils.qqyyyy(fd);
		}
		switch (ltk) {
		case "01CNKD":
			p2 = "BÁO CÁO ĐỐI CHIẾU TỜ KHAI 01/CNKD";
			tin = "mv_tin_cn";
			break;
		case "03GTGT":
			p2 = "BÁO CÁO ĐỐI CHIẾU TỜ KHAI 03/GTGT";
			tin = "mv_tin_cn";
			break;
		default:
			p2 = "BÁO CÁO ĐỐI CHIẾU TỜ KHAI 04/GTGT";
			tin = "mv_tin";
		}
		StringBuffer wh = new StringBuffer(" where a.taxo=?");
		List<Object> param = new ArrayList<Object>();
		param.add(cqt);
		if (StringUtils.isNotBlank(mst)) {
			List<String> tins = new ArrayList<String>(Arrays.asList(mst.split(",")));
			wh.append(String.format(" and tin in (%s)", String.join(",", Collections.nCopies(tins.size(), "?"))));
			param.addAll(tins);
		}
		switch (lbc) {
		case "1":
			String lhkt = r.getLhkt();
			if (!"0".equals(lhkt)) {
				param.add(lhkt);
				wh.append(" and a.legal_enty=?");
				ten = sys.get_ten_lhkt(lhkt);
			}
			p4 = String.format("LHKT: %s ", ten);
			bcode = "ma";
			bname = "ten";
			btab = "dm_lhkt";
			acode = "legal_enty";
			break;
		case "2":
			String lnnt = r.getLnnt();
			if (!"0".equals(lnnt)) {
				param.add(lnnt);
				wh.append(" and a.bpkind=?");
				ten = sys.get_ten_lnnt(lnnt);
			}
			p4 = String.format("Loại NNT: %s ", ten);
			bcode = "loai";
			bname = "ten";
			btab = "dm_loai_nnt";
			acode = "bpkind";
			break;
		default:
			List<String> nnkds = r.getNnkd();
			if (!Utils.isEmpty(nnkds)) {
				StringBuffer nn = new StringBuffer();
				StringBuffer sb = new StringBuffer();
				for (String nnkd : nnkds) {
					nn.append("," + nnkd);
					param.add(nnkd + "%");
					sb.append(" or a.ind_sector like ?");
				}
				wh.append(" and (");
				wh.append(sb.substring(3));
				wh.append(")");
				ten = nn.substring(1);
			}
			p4 = String.format("NNKD: %s ", ten);
			bcode = "ma_nnkd";
			bname = "ten_nnkd";
			btab = "dm_nganhktqd";
			acode = "ind_sector";
		}
		param.add(ltk);
		param.add(pk);
		param.add(pk);
		// @formatter:off
		StringBuffer sb = new StringBuffer("with tin as (select a.tin mst,a.oname name,b." + bname + " ten from " + tin + " a," + btab + " b" + wh + " and a." + acode + "=b." + bcode + ")");
		sb.append(",tk as (select a.mst,sum(a.amt) a1 from tin," + tk + " a where a.code=? and a." + col + "=? and tin.mst=a.mst group by a.mst)");
		sb.append(",hd as (select a.mst,sum(a.amt) a2 from " + hd + " a,tk where a."+ col +"=? and a.mst=tk.mst and a.khmshdon in (2,5) and a.tthai in (1,2,3,5) and a.ttxly in (5,6,8) group by a.mst)"); 
		sb.append(" select /*+ parallel(16) */ tin.*,a1,a2,a1-a2 a3 from (select mst,sum(a1) a1,sum(a2) a2 from (select mst,0 a1,a2 from hd union all select mst,a1,0 a2 from tk) group by mst) x,tin where x.mst=tin.mst");
		// @formatter:on
		String sql = sb.toString();
		Object[] objs = param.toArray(new Object[0]);
		// log.info(sql);
		// log.info(Arrays.toString(objs));
		String tencha = ten_cqt_cha(cqt), tencqt = sys.get_ten_cqt(cqt);
		String[] header = { tencha.toUpperCase(), tencqt.toUpperCase(), p2, p3, p4 };
		xls(r, sql, objs, header, 9);
	}

	private void r19(Report r) {
		String fd = r.getFd(), cqt = r.getCqt(), lky = r.getLky();
		String ltk = r.getLtk(), pk, col, tk, hd, p2;
		if ("month".equals(lky)) {
			pk = Utils.yymm(fd);
			col = "mm";
			tk = "mv_tk_mm";
			hd = "1".equals(ltk) ? "mv_sls_mm" : "mv_buy_mm";
			p2 = "Tháng: " + Utils.mmyyyy(fd);
		} else {
			pk = Utils.yyq(fd);
			col = "qq";
			tk = "mv_tk_qq";
			hd = "1".equals(ltk) ? "mv_sls_qq" : "mv_buy_qq";
			p2 = "Quý: " + Utils.qqyyyy(fd);
		}
		List<Object> param = new ArrayList<Object>();
		param.add(cqt);
		StringBuffer sb = new StringBuffer();
		// @formatter:off
		if ("0000".equals(cqt)) {
			sb.append("with tax as (select a.ma_cqt,b.ma_cqt taxo,b.ten_cqt_dai cqt from ztb_map_cqt a,(select ma_cqt,ten_cqt_dai from ztb_map_cqt where MANDT='500' and ma_cha_4=? and ma_cqt like '%00') b where a.MANDT='500' and (a.ma_cqt=b.ma_cqt or a.ma_cha_4=b.ma_cqt)),tin as (select a.tin mst,b.taxo,b.cqt from mv_tin a,tax b where a.taxo=b.ma_cqt)");
		}	
		else {
			sb.append("with tax as (select ma_cqt,ten_cqt_dai from ztb_map_cqt where MANDT='500' and (ma_cqt=? or ma_cha_4=?)),tin as (select a.tin mst,b.ma_cqt taxo,b.ten_cqt_dai cqt from mv_tin a,tax b where a.taxo=b.ma_cqt)");
			param.add(cqt);
		}	
		sb.append(",tk as (select tin.mst,");
		sb.append("1".equals(ltk) ? "sum(amt) da,sum(tax)" : "sum(amt_buy) da,sum(tax_buy)");
		sb.append(" dt from tin," + tk + " where tin.mst=" + tk + ".mst and " + col + "=? group by tin.mst)");
		sb.append(",hd as (select a.mst,sum(amt) a,sum(tax) t from " + hd + " a,tk where a." + col +"=? and a.mst=tk.mst and a.khmshdon in (1,5) and a.tthai in (1,2,3,5) and a.ttxly in (5,6,8) group by a.mst)");
		sb.append(",kq as (select tin.*,da,a,da-a ca,dt,t,dt-t ct from (select mst,sum(da) da,sum(dt) dt,sum(a) a,sum(t) t from (select mst,0 da,0 dt,a,t from hd union all select mst,da,dt,0 a,0 t from tk) group by mst) x,tin where x.mst=tin.mst)");
		sb.append(" select /*+ parallel(16) */ taxo,cqt,count(case when ca<>0 or ct<>0 then 1 end) cnt,sum(da) da,sum(a) a,sum(ca) ca,sum(dt) dt,sum(t) t,sum(ct) ct from kq group by taxo,cqt");
		param.add(pk);
		param.add(pk);
		Object[] objs = param.toArray(new Object[0]);
		String sql = sb.toString();
		log.info(sql);
		log.info(Arrays.toString(objs));
		String tencha = ten_cqt_cha(cqt), tencqt = sys.get_ten_cqt(cqt), p1 = "BÁO CÁO TỔNG HỢP KẾT QUẢ ĐỐI CHIẾU TỜ KHAI 01/GTGT " + ("1".equals(ltk) ? "BÁN RA" : "MUA VÀO");
		// @formatter:on
		String[] header = { tencha.toUpperCase(), tencqt.toUpperCase(), p1, p2 };
		xls(r, sql, objs, header, 8);
	}

	private void process(Report r) {
		Instant instant = Instant.now();
		String code = r.getCode(), cqt = r.getCqt();
		switch (code) {
		case "r3":
			r3_4(r);
			break;
		case "r4":
			r3_4(r);
			break;
		case "r5":
			r5(r);
			break;
		case "r6":
			r6(r);
			break;
		case "r7":
			r7(r);
			break;
		case "r8":
			r8(r);
			break;
		case "r10":
			r10(r);
			break;
		case "r11":
			r11(r);
			break;
		case "r12":
			r12(r);
			break;
		case "r13":
			r13(r);
			break;
		case "r14":
			r14(r);
			break;
		case "r15":
			r15(r);
			break;
		case "r16":
			r16(r);
			break;
		case "r17":
			r17(r);
			break;
		case "r18":
			r18(r);
			break;
		case "r19":
			r19(r);
			break;
		default:
			r1_2(r);
		}
		log.info("process {}, {}, {}", code, cqt, Duration.between(instant, Instant.now()).toSeconds());
	}
}