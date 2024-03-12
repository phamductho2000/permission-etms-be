package com.fis.invoice.api;

import com.fis.invoice.dto.AdminGroupUserDTO;
import com.fis.invoice.dto.DemoTableDTO;
import com.fis.invoice.service.AdminGroupUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/AdminGroupUser")
public class AdminGroupUserController {
    private final Logger log = LoggerFactory.getLogger(AdminGroupUserController.class);

    private final AdminGroupUserService adminGroupUserService;

    public AdminGroupUserController(AdminGroupUserService adminGroupUserService) {
        this.adminGroupUserService = adminGroupUserService;
    }

    @GetMapping("/getAll")
    public ResponseEntity<?> getAllAdminGroupUser() throws Exception {
        log.debug("REST request to get a page of adminGroupUser");
        List<AdminGroupUserDTO> list =  adminGroupUserService.findAll();
        return ResponseEntity.ok(list);
    }
}
