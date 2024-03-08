package com.fis.invoice.domain;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SearchResult {
	 private int total;
	 private List<?> list;
}
