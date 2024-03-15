package com.fis.invoice.service;

import com.fis.invoice.domain.UserRole;
import com.fis.invoice.dto.TblUsersDTO;
import com.fis.invoice.dto.UserRoleDTO;
import com.fis.invoice.repository.UserRoleRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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


    // tạo mới
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public UserRoleDTO create(UserRoleDTO userRoleDTO) throws Exception {
        log.debug("Request to creat userRoleDTO");
        try {
            UserRole userRole = new UserRole();
            BeanUtils.copyProperties(userRoleDTO, userRole);
            userRole = userRoleRepository.save(userRole);
            BeanUtils.copyProperties(userRole, userRoleDTO);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return userRoleDTO;
    }
    // Update
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public UserRoleDTO update(UserRoleDTO userRoleDTO) throws Exception {
        log.debug("Request to update userRole");
        try {
            UserRole userRole = new UserRole();
            userRole.getUserId();
            userRole.setAreaCode(userRole.getAreaCode());
            BeanUtils.copyProperties(userRoleDTO,userRole);
            userRoleRepository.save(userRole);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return userRoleDTO;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public List<UserRoleDTO> findAllUserRoleGroupId(UserRoleDTO userRoleDTO) throws Exception {
        log.debug("Request to get findAllUserRoleGroupId");

        return userRoleRepository.findAllByUserId(userRoleDTO.getUserId()).stream()
                .map(existing -> modelMapper.map(existing, UserRoleDTO.class))
                .collect(Collectors.toList());
    }
}
