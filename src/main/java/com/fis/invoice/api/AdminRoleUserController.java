package com.fis.invoice.api;

import com.fis.invoice.dto.*;
import com.fis.invoice.service.AdminRoleUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/AdminRollUser")
public class AdminRoleUserController {
    private final Logger log = LoggerFactory.getLogger(AdminGroupUserController.class);

    private final AdminRoleUserService adminGroupUserService;

    public AdminRoleUserController(AdminRoleUserService adminGroupUserService) {
        this.adminGroupUserService = adminGroupUserService;
    }


    @GetMapping("/getAllAdminRollUser")
    public ResponseEntity<?> getAllAdminRoleUser() throws Exception {
        log.debug("REST request to get a page of AdminRoleUser");
        List<AdminRoleUserDTO> list =  adminGroupUserService.findAll();
        return ResponseEntity.ok(list);
    }

    @PutMapping("/updateAdminRollUser")
    public ResponseEntity<RegRoleAdminUserDTO> updateRoleAdminUserDTO (@RequestBody RegRoleAdminUserDTO req) throws Exception {
        Optional<RegRoleAdminUserDTO> result = Optional.ofNullable((RegRoleAdminUserDTO) adminGroupUserService.updateRoleToTbl(req));
        if(result.isEmpty()) {
            throw new Exception("Cập nhật không thành công");
        }
        result.get().setSuccess(true);
        return ResponseEntity.ok(result.orElse(null));
    }

    @PostMapping("/findAllUserAdminByGroupId")
    public ResponseEntity<List<AdminRoleUserDTO>> findAllUserAdminByGroupId(@RequestBody  AdminRoleUserDTO req ) throws Exception {
        log.debug("REST request to findAllUserByGroupId  : {}", req);
        return ResponseEntity.ok(adminGroupUserService.findAllUserAdminByGroupId(req));
    }
}
