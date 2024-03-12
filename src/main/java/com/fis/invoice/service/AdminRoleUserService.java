package com.fis.invoice.service;

import com.fis.invoice.dto.AdminRoleUserDTO;
import com.fis.invoice.repository.AdminRoleUserRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
    private ModelMapper modelMapper;

    @Transactional (readOnly = true)
    public List<AdminRoleUserDTO> findAll() throws Exception {
        log.debug("Request to get all AdminRoleUser");
        return adminRoleUserRepository.findAll().stream().map(exitting -> modelMapper.map(exitting, AdminRoleUserDTO.class)).collect(Collectors.toList());
    }
}
