package com.fis.invoice.api;

import com.fis.invoice.dto.UserRoleDTO;
import com.fis.invoice.service.UserRoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/UserRole")
public class UserRoleController {
    private final Logger log = LoggerFactory.getLogger(UserRoleController.class);

    private final UserRoleService userRoleService;

    public UserRoleController(UserRoleService userRoleService) {
        this.userRoleService = userRoleService;
    }


    @GetMapping("/getAll")
    public ResponseEntity<?> getAllUserRole() throws Exception {
        log.debug("REST request to get a page of DemoTable");
        List<UserRoleDTO> list =  userRoleService.findAll();
        return ResponseEntity.ok(list);
    }
}
