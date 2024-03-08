package com.fis.invoice.api;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDateTime;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fis.invoice.domain.Report;
import com.fis.invoice.domain.Search;
import com.fis.invoice.domain.SearchResult;
import com.fis.invoice.domain.User;
import com.fis.invoice.service.RepService;
import com.fis.invoice.service.Utils;

@RestController
@RequestMapping("/api/rep")
public class RepApi {
	@Autowired
	private RepService repService;
	@Value(value = "${xls.path}")
	private String PATH;

	@PostMapping(value = "/send")
	public void send(@RequestBody Report r) throws Exception{
		Utils.hasRole(r.getCode());
		User user = Utils.getCurrentUser();
		r.setUsr(user.getUsername());
		if (StringUtils.isBlank(r.getCqt()))
			r.setCqt(user.getCqt());
		r.setDt(LocalDateTime.now());
		r.setXls(UUID.randomUUID().toString());
		repService.put(r);
	}

	@PostMapping("/search")
	public ResponseEntity<SearchResult> search(@RequestBody Search s) throws Exception {
		SearchResult result = repService.search(s);
		return new ResponseEntity<SearchResult>(result, HttpStatus.OK);
	}

	@GetMapping(value = "/download/{filename}")
	public ResponseEntity<Resource> download(@PathVariable(name = "filename", required = true) String filename)
			throws Exception {
		String path = PATH + filename + ".zip";
		File file = new File(path);
		if (!file.exists() || !file.isFile())
			throw new Exception(String.format("Không tìm thấy file %s", filename));
		InputStreamResource inputStreamResource = new InputStreamResource(new FileInputStream(file));
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentLength(file.length());
		httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		return new ResponseEntity<Resource>(inputStreamResource, httpHeaders, HttpStatus.OK);
	}

	@PostMapping("/del/{id}")
	public ResponseEntity<Integer> del(@PathVariable(required = true) String id) throws Exception {
		return new ResponseEntity<Integer>(repService.del(id), HttpStatus.OK);
	}
}
