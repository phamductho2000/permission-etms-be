package com.fis.invoice.api;

import com.fis.invoice.dto.AdminGroupUserDTO;
import com.fis.invoice.dto.AdminRoleUserDTO;
import com.fis.invoice.service.AdminRoleUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/AdminRollUser")
public class AdminRoleUserController {
    private final Logger log = LoggerFactory.getLogger(AdminGroupUserController.class);

    private final AdminRoleUserService adminGroupUserService;

    public AdminRoleUserController(AdminRoleUserService adminGroupUserService) {
        this.adminGroupUserService = adminGroupUserService;
    }


    @GetMapping("/getAll")
    public ResponseEntity<?> getAllAdminRoleUser() throws Exception {
        log.debug("REST request to get a page of AdminRoleUser");
        List<AdminRoleUserDTO> list =  adminGroupUserService.findAll();
        return ResponseEntity.ok(list);
    }
}
