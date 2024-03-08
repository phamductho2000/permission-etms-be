package com.fis.invoice.domain;

import lombok.Data;


@Data
public class Search {
	private int page = 0;
	private int limit = 10;
	private String order;
	//invoice
	private String fd;
	private String td;
	private String nbmst;
	private String nmmst;
	private String khmshdon;
	private String khhdon;
	private String shdon;
	private String tthai;
	private String tchat;
	private String nbten;
	private String nmten;
	
	//user
	private String usr;
	private String name;
	private String status;
	private String cqt;
	private String cucthue;
	
	private String rid;
	//report
	private String code;
	//nnt 
	private String mst;
	private String source;
	private String dt;
}
