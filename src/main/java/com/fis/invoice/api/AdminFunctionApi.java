package com.fis.invoice.api;

import com.fis.invoice.dto.AdminFuncDTO;
import com.fis.invoice.service.AdminFunctionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/function")
public class AdminFunctionApi {
    private final Logger log = LoggerFactory.getLogger(AdminFunctionApi.class);

    @Autowired
    private AdminFunctionService adminFunctionService;

    @PostMapping("/create")
    public ResponseEntity<AdminFuncDTO> create(@RequestBody AdminFuncDTO adminFuncDTO) throws Exception {
        return new ResponseEntity<>(adminFunctionService.createFunc(adminFuncDTO), HttpStatus.OK);
    }

    @GetMapping("/getAll")
    public ResponseEntity<?> getAllAdminFunc() throws Exception {
        log.debug("REST request to get a page of adminGroupUser");
        List<AdminFuncDTO> list =  adminFunctionService.findAll();
        return ResponseEntity.ok(list);
    }
}
