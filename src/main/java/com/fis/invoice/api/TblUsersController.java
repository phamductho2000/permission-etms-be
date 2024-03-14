package com.fis.invoice.api;

import com.fis.invoice.dto.TblUsersDTO;
import com.fis.invoice.service.TblUsersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/TblUsers")
public class TblUsersController {
    private final Logger log = LoggerFactory.getLogger(TblUsersController.class);

    private final TblUsersService tblUsersService;

    public TblUsersController(TblUsersService tblUsersService) {
        this.tblUsersService = tblUsersService;
    }


    @GetMapping("/getAll")
    public ResponseEntity<?> getAllTblUsers() throws Exception {
        log.debug("REST request to get a page of TblUsers");
        List<TblUsersDTO> list =  tblUsersService.findAll();
        return ResponseEntity.ok(list);
    }

    @PostMapping("/getAllBySearch")
    public ResponseEntity<?> getAllBySearch(@RequestBody TblUsersDTO tblUsersDTO) throws Exception {
        String userName = tblUsersDTO.getUsername();
        List<TblUsersDTO> list =  tblUsersService.findAllBySearch(userName);
        return ResponseEntity.ok(list);
    }

    @PutMapping("/updateTblUser")
    public ResponseEntity<?> updateTblUser( @RequestBody TblUsersDTO tblUsersDTO) throws Exception {
        return ResponseEntity.ok(tblUsersDTO);
    }
}
