package com.fis.invoice.api;

import com.fis.invoice.dto.UserRoleDTO;
import com.fis.invoice.service.UserRoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
        log.debug("REST request to get a page of UserRole");
        List<UserRoleDTO> list =  userRoleService.findAll();
        return ResponseEntity.ok(list);
    }

    @PostMapping("/create")
    public ResponseEntity<UserRoleDTO> createUserRole(@RequestBody UserRoleDTO userRoleDTO) throws Exception {
        return new ResponseEntity<>(userRoleService.create(userRoleDTO), HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<UserRoleDTO> updateUserRole(@RequestBody UserRoleDTO userRoleDTO) throws Exception {
        Optional<UserRoleDTO> result = Optional.ofNullable(userRoleService.update(userRoleDTO));
        if(result.isPresent()) {
            throw new Exception("Cập nhật không thành công");
        }
        return ResponseEntity.ok(userRoleDTO);
    }
}
