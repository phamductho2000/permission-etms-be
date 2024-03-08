package com.fis.invoice.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Dtl {
	private Integer rn;
	private String stt;
	private String tchat;
	private String ldchinh;
	private String mhhdvu;
	private String ten;
	private String dvtinh;
	private Double dgia;
	private Double sluong;
	private Double thtien;
	private String ltsuat;
	private Double tsuat;
	private Double tlckhau;
	private Double stckhau;
}