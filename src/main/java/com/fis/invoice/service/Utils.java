package com.fis.invoice.service;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.fis.invoice.domain.Kv;
import com.fis.invoice.domain.UnauthorizedException;
import com.fis.invoice.domain.User;

public class Utils {
	private static final DateTimeFormatter TF = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
	private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private static final DateTimeFormatter YY = DateTimeFormatter.ofPattern("yy");
	// private static final DateTimeFormatter YYQQ =
	// DateTimeFormatter.ofPattern("yy'Q'Q");
	private static final DateTimeFormatter YYQ = DateTimeFormatter.ofPattern("yyQ");
	private static final DateTimeFormatter YYMM = DateTimeFormatter.ofPattern("yyMM");
	private static final DateTimeFormatter YYYY = DateTimeFormatter.ofPattern("yyyy");
	private static final DateTimeFormatter MMYYYY = DateTimeFormatter.ofPattern("MM/yyyy");
	private static final DateTimeFormatter QQYYYY = DateTimeFormatter.ofPattern("Q/yyyy");

	public static String find(List<Kv> list, String str) {
		Kv kv = list.stream().filter(e -> str.equals(e.getValue())).findAny().orElse(null);
		return kv != null ? kv.getLabel() : "";
	}

	public static boolean isEmpty(List<?> list) {
		return list == null || list.isEmpty();
	}

	public static Date s2addmonth(String str, long m) {
		return Date.valueOf(LocalDate.parse(str, DTF).plusMonths(m));
	}

	public static Date s2nextdate(String str) {
		return Date.valueOf(LocalDate.parse(str, DTF).plusDays(1));
	}

	public static Date s2d(String str) {
		if (StringUtils.isBlank(str))
			return null;
		return Date.valueOf(LocalDate.parse(str, DTF));
	}

	public static String yymm(String str) {
		return LocalDate.parse(str, DTF).format(YYMM);
	}

	public static String nexty(String str) {
		return LocalDate.parse(str, DTF).plusYears(1).format(YY);
	}

	public static String prevy(String str) {
		return LocalDate.parse(str, DTF).minusYears(1).format(YY);
	}

	public static String nextq(String str) {
		return LocalDate.parse(str, DTF).plusMonths(3).format(YYQ);
	}

	public static String prevq(String str) {
		return LocalDate.parse(str, DTF).minusMonths(3).format(YYQ);
	}

	public static String nextm(String str) {
		return LocalDate.parse(str, DTF).plusMonths(1).format(YYMM);
	}

	public static String prevm(String str) {
		return LocalDate.parse(str, DTF).minusMonths(1).format(YYMM);
	}

	public static String prev3m(String str) {
		return LocalDate.parse(str, DTF).minusMonths(3).format(YYMM);
	}

	public static String prev12m(String str) {
		return LocalDate.parse(str, DTF).minusMonths(12).format(YYMM);
	}

	public static String yy(String str) {
		return LocalDate.parse(str, DTF).format(YY);
	}

	public static String yyyy(String str) {
		return LocalDate.parse(str, DTF).format(YYYY);
	}

	public static String mmyyyy(String str) {
		return LocalDate.parse(str, DTF).format(MMYYYY);
	}

	public static String qqyyyy(String str) {
		return LocalDate.parse(str, DTF).format(QQYYYY);
	}

	// public static String yyqq(String str) {
	// return LocalDate.parse(str, DTF).format(YYQQ);
	// }

	public static String yyq(String str) {
		return LocalDate.parse(str, DTF).format(YYQ);
	}

	public static String d2s(Date date) {
		if (date == null)
			return null;
		return date.toLocalDate().format(DTF);
	}

	public static String timestamp2s(Timestamp ts) {
		if (ts == null)
			return null;
		return ts.toLocalDateTime().format(TF);
	}

	public static String now() {
		return LocalDate.now().format(DTF);
	}

	public static User getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = (User) authentication.getPrincipal();
		return user;
	}

	public static void hasRole(String role) throws UnauthorizedException {
		if (!getCurrentUser().getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(role)))
			throw new UnauthorizedException();
	}
}
