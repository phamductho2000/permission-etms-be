package com.fis.invoice.domain;

import java.util.List;

import lombok.Data;
@Data
public class GrantRole {
	private String rid;
	private List<String> usrs;
	private List<String> list;

}
