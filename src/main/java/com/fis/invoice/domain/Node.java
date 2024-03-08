package com.fis.invoice.domain;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Node implements Serializable{
	private static final long serialVersionUID = 1L;
	private String value;
	private String pid;
	private String title;
	private List<Node> children;
}