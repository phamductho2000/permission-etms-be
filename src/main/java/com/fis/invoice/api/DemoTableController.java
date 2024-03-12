package com.fis.invoice.api;

import com.fis.invoice.dto.DemoTableDTO;
import com.fis.invoice.service.DemoTableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class DemoTableController {
    private final Logger log = LoggerFactory.getLogger(DemoTableController.class);

    private final DemoTableService demoTableService;

    public DemoTableController(DemoTableService demoTableService) {
        this.demoTableService = demoTableService;
    }

    @GetMapping("/getAll")
    public ResponseEntity<?> getAlldemoTable() throws Exception {
        log.debug("REST request to get a page of DemoTable");
        List<DemoTableDTO> list =  demoTableService.findAll();
        return ResponseEntity.ok(list);
    }
}
