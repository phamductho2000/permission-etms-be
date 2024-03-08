package com.fis.invoice.domain;



import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Role implements Serializable{
	private static final long serialVersionUID = 1L;
	private String rid;
	private String name;
}
