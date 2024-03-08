package com.fis.invoice.domain;

import java.util.List;

import lombok.Data;
@Data
public class GrantUser {
	private String usr;
	private List<String> rids;
}
