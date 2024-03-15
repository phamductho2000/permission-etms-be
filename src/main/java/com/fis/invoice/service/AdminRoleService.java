package com.fis.invoice.service;

import com.fis.invoice.domain.AdminRole;
import com.fis.invoice.dto.AdminRoleDTO;
import com.fis.invoice.repository.AdminRoleRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminRoleService {

    private final Logger log = LoggerFactory.getLogger(AdminRoleService.class);

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AdminRoleRepository adminRoleRepository;

    @Transactional(readOnly = true)
    public List<AdminRoleDTO> findAll() throws Exception {
        log.debug("Request to get all AdminRole");
        return adminRoleRepository.findAll().stream().map(exitting -> modelMapper.map(exitting, AdminRoleDTO.class)).collect(Collectors.toList());
    }

    // tạo mới
    public AdminRoleDTO create(AdminRoleDTO adminRoleDTO) throws Exception {
        log.debug("Request to creat adminRoleFunction");
        try {
            AdminRole adminRole = new AdminRole();
            BeanUtils.copyProperties(adminRoleDTO, adminRole);
            adminRole = adminRoleRepository.save(adminRole);
            BeanUtils.copyProperties(adminRole, adminRoleDTO);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return adminRoleDTO;
    }
    // Update
    @Transactional
    public AdminRoleDTO update(AdminRoleDTO adminRoleDTO) throws Exception {
        log.debug("Request to update adminRoleFunction");
        try {
            AdminRole adminRole = new AdminRole();
            BeanUtils.copyProperties(adminRoleDTO,adminRole);
            adminRoleRepository.save(adminRole);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return adminRoleDTO;
    }

    public boolean delete(Long id) throws Exception {
        log.debug("Request to delete adminRole : {}", id);
        adminRoleRepository.deleteById(id);
        return true;
    }


    public List<AdminRoleDTO> findById(Long id) throws Exception {
        log.debug("Request to findById AdminRole {}", id);
        return adminRoleRepository.findById(id).stream().map(exiting -> modelMapper.map(exiting, AdminRoleDTO.class)).collect(Collectors.toList());
    }
}
