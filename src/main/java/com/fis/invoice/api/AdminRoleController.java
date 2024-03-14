package com.fis.invoice.api;

import com.fis.invoice.dto.AdminRoleDTO;
import com.fis.invoice.service.AdminRoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/adminRole")
public class AdminRoleController {
    private final Logger log = LoggerFactory.getLogger(AdminRoleController.class);

    @Autowired
    private AdminRoleService adminRoleService;

    @GetMapping("/getAll")
    public ResponseEntity<?> getAllAdminRole() throws Exception {
        log.debug("REST request to get a page of AdminRole");
        List<AdminRoleDTO> list =  adminRoleService.findAll();
        return ResponseEntity.ok(list);
    }


    @PostMapping("/create")
    public ResponseEntity<AdminRoleDTO> createAdminRole(@RequestBody AdminRoleDTO adminRoleDTO) throws Exception {
        return new ResponseEntity<>(adminRoleService.create(adminRoleDTO), HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<AdminRoleDTO> updateAdminRole(@RequestBody AdminRoleDTO adminRoleDTO) throws Exception {
        Optional<AdminRoleDTO> result = Optional.ofNullable(adminRoleService.update(adminRoleDTO));
        if (result.isPresent()) {
            throw new Exception("Cập nhật không thành công");
        }
        return ResponseEntity.ok(adminRoleDTO);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteAdminRole(@PathVariable(value = "id", required = true) Long id) throws Exception {
        log.debug("REST request to delete QthtChucnang : {}", id);
        boolean result = adminRoleService.delete(id);
        if (!result){
            throw new Exception("Xoa ban ghi that bai");
        }
        return ResponseEntity.ok("Xoa ban ghi thanh cong");
    }

    @GetMapping("/getAllById/{id}")
    public ResponseEntity<List<AdminRoleDTO>> getAdminRoleById(@PathVariable Long id) throws Exception {
        log.debug("Rest request to findById getAllById {}", id);
        List<AdminRoleDTO> list =  adminRoleService.findById(id);
        return ResponseEntity.ok(list);
    }
}
