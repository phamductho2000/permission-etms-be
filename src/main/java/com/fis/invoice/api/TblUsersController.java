package com.fis.invoice.api;

import com.fis.invoice.dto.AdminRoleUserDTO;
import com.fis.invoice.dto.RegRoleAdminUserDTO;
import com.fis.invoice.dto.ReqTblUserDTO;
import com.fis.invoice.dto.TblUsersDTO;
import com.fis.invoice.service.TblUsersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
    public ResponseEntity<ReqTblUserDTO> updateRoleAdminUserDTO (@RequestBody ReqTblUserDTO req) throws Exception {
        Optional<ReqTblUserDTO> result = Optional.ofNullable((ReqTblUserDTO) tblUsersService.updateTblUser(req));
        if(result.isEmpty()) {
            throw new Exception("Cập nhật không thành công");
        }
        result.get().setSuccess(true);
        return ResponseEntity.ok(result.orElse(null));
    }


    @PostMapping("/findAllTblUserGroupId")
    public ResponseEntity<List<TblUsersDTO>> findAllTblUserGroupId(@RequestBody  TblUsersDTO req ) throws Exception {
        log.debug("REST request to findAllUserByGroupId  : {}", req);
        return ResponseEntity.ok(tblUsersService.findAllTblUserGroupId(req));
    }
}
