package com.fis.invoice.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Err {
	private String type;
	private String idhdon;
	private String stt;
	private String nbmst;
	private String khmshdon;
	private String khhdon;
	private String shdon;
	private String nhom;
	private String err;
	private String dt;
}