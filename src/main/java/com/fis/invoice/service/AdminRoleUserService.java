package com.fis.invoice.service;

import com.fis.invoice.domain.AdminRoleUser;
import com.fis.invoice.domain.TblUsers;
import com.fis.invoice.dto.AdminRoleFunctionDTO;
import com.fis.invoice.dto.AdminRoleUserDTO;
import com.fis.invoice.dto.RegRoleAdminUserDTO;
import com.fis.invoice.repository.AdminRoleUserRepository;
import com.fis.invoice.repository.TblUsersRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminRoleUserService {
    private final Logger log = LoggerFactory.getLogger(AdminRoleUserService.class);


    @Autowired
    private AdminRoleUserRepository adminRoleUserRepository;

    @Autowired
    private TblUsersRepository tblUsersRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public List<AdminRoleUserDTO> findAll() throws Exception {
        log.debug("Request to get all AdminRoleUser");
        return adminRoleUserRepository.findAll().stream()
                .map(exitting -> {
                    AdminRoleUserDTO dto = new AdminRoleUserDTO();
                    // check data trong db có trường nào bị null ko
                    if (exitting != null)
                        dto = modelMapper.map(exitting, AdminRoleUserDTO.class);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public RegRoleAdminUserDTO updateRoleToTbl(RegRoleAdminUserDTO req) throws Exception {
        log.debug("Request to update adminRoleFunction");
        try {
            // find all function by role id
            List<AdminRoleUser> adminRoleUsers = adminRoleUserRepository.findAllByRoleId(req.getRoleId());
            if (!adminRoleUsers.isEmpty()) {
                // remove all
                adminRoleUserRepository.deleteAll(adminRoleUsers);
            }

            // insert new
            List<Integer> UserIds = req.getTblUsersDTOS().stream().map(f -> f.getUserId()).collect(Collectors.toList());
            if (!UserIds.isEmpty()) {
                List<TblUsers> tblUsers = tblUsersRepository.findAllById(UserIds);
                if (!tblUsers.isEmpty()) {
                    tblUsers.forEach(func -> {
                        AdminRoleUser adminRoleUser = new AdminRoleUser();
                        adminRoleUser.setRoleId(req.getRoleId());
                        adminRoleUser.setUserId(func.getUserId());
                        adminRoleUserRepository.save(adminRoleUser);
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return req;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public List<AdminRoleUserDTO> findAllUserAdminByGroupId(AdminRoleUserDTO adminRoleUserDTO) throws Exception {
        log.debug("Request to get findAllUserByGroupId");

        return adminRoleUserRepository.findAllRoleUserGroupId(adminRoleUserDTO.getRoleId()).stream()
                .map(existing -> modelMapper.map(existing, AdminRoleUserDTO.class))
                .collect(Collectors.toList());
    }
}
