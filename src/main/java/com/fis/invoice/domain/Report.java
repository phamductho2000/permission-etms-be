package com.fis.invoice.domain;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class Report {
	private Long id;
	private LocalDateTime dt;
	private String khmshdon;
	private String xls;
	private String err;
	private String usr;
	private String code;
	private String cqt;
	private String fd;
	private String td;
	private String lbc;
	private String lky;
	private String ltk;
	private String lsl;
	private String lhkt;
	private String lnnt;
	private List<String> nnkd;
	private String mst;
	private String ft;
	private String tt;
	private Integer fv;
	private Integer tv;
	private Long elapsed;
	private Integer rn;
	private Integer ntitle;
	private String tthai;
	private String name;
	private String loaidl;
}