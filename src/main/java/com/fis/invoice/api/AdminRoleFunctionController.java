package com.fis.invoice.api;

import com.fis.invoice.dto.AdminFuncDTO;
import com.fis.invoice.dto.AdminRoleDTO;
import com.fis.invoice.dto.AdminRoleFunctionDTO;
import com.fis.invoice.dto.ReqRoleAdminFuncDTO;
import com.fis.invoice.service.AdminRoleFunctionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/adminRoleFuncition")
public class AdminRoleFunctionController {
    private final Logger log = LoggerFactory.getLogger(AdminRoleFunctionController.class);

    @Autowired
    private AdminRoleFunctionService adminRoleFunctionService;

    @GetMapping("/getAlladminRoleFuncition")
    public ResponseEntity<?> getAllAdminRoleFunction() throws Exception {
        log.debug("REST request to get a page of AdminRoleFunction");
        List<AdminRoleFunctionDTO> list =  adminRoleFunctionService.findAll();
        return ResponseEntity.ok(list);
    }

    @PostMapping("/create")
    public ResponseEntity<AdminRoleFunctionDTO> createAdminRoleFunction (@RequestBody AdminRoleFunctionDTO adminRoleFunctionDTO) throws Exception {
        return new ResponseEntity<>(adminRoleFunctionService.create(adminRoleFunctionDTO), HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<ReqRoleAdminFuncDTO> updateRoleAdminFuncDTO (@RequestBody ReqRoleAdminFuncDTO req) throws Exception {
        Optional<ReqRoleAdminFuncDTO> result = Optional.ofNullable((ReqRoleAdminFuncDTO) adminRoleFunctionService.updateFunctionToRole(req));
        if(result.isEmpty()) {
            throw new Exception("Cập nhật không thành công");
        }
        result.get().setSuccess(true);
        return ResponseEntity.ok(result.orElse(null));
    }

    @PostMapping("/findAllUserByGroupId")
    public ResponseEntity<List<AdminRoleFunctionDTO>> findAllUserByGroupId(@RequestBody  AdminRoleFunctionDTO req ) throws Exception {
        log.debug("REST request to findAllUserByGroupId  : {}", req);
        return ResponseEntity.ok(adminRoleFunctionService.findAllUserByGroupId(req));
    }
}
