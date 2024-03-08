package com.fis.invoice.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fis.invoice.domain.Dobj;
import com.fis.invoice.domain.Dsearch;
import com.fis.invoice.domain.Dtl;
import com.fis.invoice.domain.Search;
import com.fis.invoice.domain.SearchResult;
import com.fis.invoice.service.InvService;
import com.fis.invoice.service.Utils;

@RestController
@RequestMapping("/api/inv")
public class InvApi {
	@Autowired
	private InvService invService;
	
	@PostMapping("/chart")
	public ResponseEntity<List<Dobj>> chart(@RequestBody Dsearch s) throws Exception {
		Utils.hasRole("r9");
		return new ResponseEntity<List<Dobj>>(invService.chart(s), HttpStatus.OK);
	}
	
	@PostMapping("/search_log")
	public ResponseEntity<SearchResult> search_log(@RequestBody Search s) throws Exception {
		return new ResponseEntity<SearchResult>( invService.search_log(s), HttpStatus.OK);
	}
	
	@PostMapping("/search_err")
	public ResponseEntity<SearchResult> search_err(@RequestBody Search s) throws Exception {
		return new ResponseEntity<SearchResult>(invService.search_err(s), HttpStatus.OK);
	}

	@PostMapping("/search_invoice")
	public ResponseEntity<SearchResult> search_invoice(@RequestBody Search s) throws Exception {
		Utils.hasRole("search");
		return new ResponseEntity<SearchResult>(invService.search_invoice(s), HttpStatus.OK);
	}

	@PostMapping("/get_dtl/{id}")
	public ResponseEntity<List<Dtl>> get_dtl(@PathVariable(required = true) String id) throws Exception {
		Utils.hasRole("search");
		return new ResponseEntity<List<Dtl>>(invService.get_dtl(id), HttpStatus.OK);
	}
}
