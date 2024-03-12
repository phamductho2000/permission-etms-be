package com.fis.invoice.service;

import com.fis.invoice.dto.UserRoleDTO;
import com.fis.invoice.repository.UserRoleRepository;
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
public class UserRoleService {

    private final Logger log = LoggerFactory.getLogger(UserRoleService.class);


    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Transactional (readOnly = true)
    public List<UserRoleDTO> findAll() throws Exception {
        log.debug("Request to get all UserRole");
        return userRoleRepository.findAll().stream().map(exitting -> modelMapper.map(exitting, UserRoleDTO.class)).collect(Collectors.toList());
    }
}
