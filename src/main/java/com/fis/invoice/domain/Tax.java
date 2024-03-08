package com.fis.invoice.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Tax {
	private String tsuat;
	private Double thtien;
	private Double tthue;
}
