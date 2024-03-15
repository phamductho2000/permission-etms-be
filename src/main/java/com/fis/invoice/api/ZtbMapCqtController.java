package com.fis.invoice.api;

import com.fis.invoice.dto.ZtbMapCqtDTO;
import com.fis.invoice.service.ZtbMapCqtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ZtbMapCqt")
public class ZtbMapCqtController {
    private final Logger log = LoggerFactory.getLogger(ZtbMapCqtController.class);

    private final ZtbMapCqtService ztbMapCqtService;

    public ZtbMapCqtController(ZtbMapCqtService ztbMapCqtService) {
        this.ztbMapCqtService = ztbMapCqtService;
    }


    @GetMapping("/getAllZtbMapCqtDTO")
    public ResponseEntity<?> getAllZtbMapCqtDTO() throws Exception {
        log.debug("REST request to get a page of ZtbMapCqtDTO");
        List<ZtbMapCqtDTO> list =  ztbMapCqtService.findAll();
        return ResponseEntity.ok(list);
    }

    @PostMapping("/ZtbMapCqtDTO")
    public ResponseEntity<ZtbMapCqtDTO> createZtbMapCqtDTO(@RequestBody ZtbMapCqtDTO ztbMapCqtDTO) throws Exception {
        return new ResponseEntity<>(ztbMapCqtService.createZtbMapCqt(ztbMapCqtDTO), HttpStatus.OK);
    }


}
