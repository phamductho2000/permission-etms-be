package com.fis.invoice.api;

import com.fis.invoice.dto.AdminGroupUserDTO;
import com.fis.invoice.dto.GroupmembersDTO;
import com.fis.invoice.service.GroupmembersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/Groupmembers")
public class GroupmembersController {
    private final Logger log = LoggerFactory.getLogger(GroupmembersController.class);

    private final GroupmembersService groupmembersService;

    public GroupmembersController(GroupmembersService groupmembersService) {
        this.groupmembersService = groupmembersService;
    }

    @GetMapping("/getAll")
    public ResponseEntity<?> getAllGroupmembers() throws Exception {
        log.debug("REST request to get a page of Groupmembers");
        List<GroupmembersDTO> list =  groupmembersService.findAllGroupmembers();
        return ResponseEntity.ok(list);
    }
}
