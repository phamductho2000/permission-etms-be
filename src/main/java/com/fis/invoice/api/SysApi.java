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

import com.fis.invoice.domain.Kv;
import com.fis.invoice.domain.Node;
import com.fis.invoice.domain.Search;
import com.fis.invoice.domain.SearchResult;
import com.fis.invoice.service.SysService;

@RestController
@RequestMapping("/api")
public class SysApi {
	@Autowired
	private SysService sysService;
	
	@PostMapping("/get_reports")
	public ResponseEntity<List<Kv>> get_reports() throws Exception{
		return ResponseEntity.ok(sysService.get_reports());
	}

	@PostMapping("/get_cuc_thue")
	public ResponseEntity<List<Kv>> get_cuc_thue() throws Exception {
		return ResponseEntity.ok(sysService.get_cuc_thue());
	}

	@PostMapping("/get_cqt/{cqt}")
	public ResponseEntity<List<Kv>> get_cqt(@PathVariable(required = true) String cqt) throws Exception {
		return ResponseEntity.ok(sysService.get_cqt_select(cqt));
	}

	@PostMapping("/get_lhkt")
	public ResponseEntity<List<Kv>> get_lhkt() throws Exception {
		return ResponseEntity.ok(sysService.get_lhkt());
	}

	@PostMapping("/get_lnnt")
	public ResponseEntity<List<Kv>> get_lnnt() throws Exception {
		return ResponseEntity.ok(sysService.get_lnnt());
	}

	@PostMapping("/get_nnkd/{type}")
	public ResponseEntity<List<Node>> get_nnkd(@PathVariable(required = true) String type) throws Exception {
		return ResponseEntity.ok(sysService.get_nnkd(type));
	}
	
	@PostMapping("/search_nnt")
	public ResponseEntity<SearchResult> search_nnt(@RequestBody Search s) throws Exception {
		return new ResponseEntity<SearchResult>(sysService.search_nnt(s), HttpStatus.OK);
	}
}
