package com.fis.invoice.service;

import com.fis.invoice.dto.AdminGroupUserDTO;
import com.fis.invoice.repository.AdminGroupUserRepository;
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
public class AdminGroupUserService {

    private final Logger log = LoggerFactory.getLogger(AdminGroupUserService.class);


    @Autowired
    private AdminGroupUserRepository adminGroupUserRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Transactional (readOnly = true)
    public List<AdminGroupUserDTO> findAll() throws Exception {
        log.debug("Request to get all AdminGroupUser");
        return adminGroupUserRepository.findAll().stream().map(exitting -> modelMapper.map(exitting, AdminGroupUserDTO.class)).collect(Collectors.toList());
    }
}
