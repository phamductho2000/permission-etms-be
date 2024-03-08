package com.fis.invoice.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fis.invoice.domain.Acc;
import com.fis.invoice.domain.GrantRole;
import com.fis.invoice.domain.GrantUser;
import com.fis.invoice.domain.Role;
import com.fis.invoice.domain.Search;
import com.fis.invoice.domain.SearchResult;
import com.fis.invoice.service.AuthService;
import com.fis.invoice.service.Utils;

@RestController
@RequestMapping("/api/auth")
public class AuthApi {
	@Autowired
	private AuthService authService;

	@PostMapping("/create_user")
	public ResponseEntity<Integer> create_user(@RequestBody Acc acc) throws Exception {
		Utils.hasRole("admin");
		return ResponseEntity.ok(authService.create_user(acc));
	}

	@PostMapping("/search_user")
	public ResponseEntity<SearchResult> search_invoice(@RequestBody Search search) throws Exception {
		Utils.hasRole("admin");
		return ResponseEntity.ok(authService.search_user(search));
	}

	@PostMapping("/update_user")
	public ResponseEntity<Integer> update_user(@RequestBody Acc acc) throws Exception {
		Utils.hasRole("admin");
		return ResponseEntity.ok(authService.update_user(acc));
	}

	@PostMapping("/get_roles")
	public ResponseEntity<List<Role>> get_roles() {
		return ResponseEntity.ok(authService.get_roles());
	}
	
	@PostMapping("/get_role_by_user/{usr}")
	public ResponseEntity<List<String>> get_role_by_user(@PathVariable(required = true) String usr) {
		Utils.hasRole("admin");
		return ResponseEntity.ok(authService.get_role_by_user(usr));
	}

	@PostMapping("/get_user_by_role")
	public ResponseEntity<List<String>> get_user_by_role(@RequestBody GrantRole gr) {
		Utils.hasRole("admin");
		return ResponseEntity.ok(authService.get_user_by_role(gr));
	}
		
	@PostMapping("/grant_by_user")
	public void grant_by_user(@RequestBody GrantUser gu) {
		Utils.hasRole("admin");
		authService.grant_by_user(gu);
	}
	
	@PostMapping("/grant_by_role")
	public void grant_by_role(@RequestBody GrantRole gr) {
		Utils.hasRole("admin");
		authService.grant_by_role(gr);
	}

}
