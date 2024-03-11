package com.fis.invoice.api;

import com.fis.invoice.dto.AdminFuncDTO;
import com.fis.invoice.service.AdminFunctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/function")
public class AdminFunctionApi {

    @Autowired
    private AdminFunctionService adminFunctionService;

    @PostMapping("/create")
    public ResponseEntity<AdminFuncDTO> create(@RequestBody AdminFuncDTO adminFuncDTO) throws Exception {
        return new ResponseEntity<>(adminFunctionService.createFunc(adminFuncDTO), HttpStatus.OK);
    }
}
