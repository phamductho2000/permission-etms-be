package com.fis.invoice.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Msg {
	private String id;
	private String nhom;
	private String cqt;
	private String nbmst;
	private String khmshdon;
	private String khhdon;
	private String shdon;
	private String status;
	private String err;
	private String dt;
}