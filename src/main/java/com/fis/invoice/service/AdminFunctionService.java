package com.fis.invoice.service;

import com.fis.invoice.domain.AdminFunction;
import com.fis.invoice.dto.AdminFuncDTO;
import com.fis.invoice.repository.AdminFunctionRepository;
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
public class AdminFunctionService {
    private final Logger log = LoggerFactory.getLogger(AdminFunctionService.class);

    @Autowired
    private AdminFunctionRepository adminFunctionRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Transactional(rollbackFor = Exception.class)
    public AdminFuncDTO createFunc(AdminFuncDTO adminFuncDTO) throws Exception {
        if (null == adminFuncDTO) {
            throw new Exception(String.format("Request null", adminFuncDTO));
        }
        AdminFunction adminFunction = new AdminFunction();
        try {
            BeanUtils.copyProperties(adminFuncDTO, adminFunction);
            adminFunction = adminFunctionRepository.save(adminFunction);
            BeanUtils.copyProperties(adminFunction, adminFuncDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return adminFuncDTO;
    }


    @Transactional (readOnly = true)
    public List<AdminFuncDTO> findAll() throws Exception {
        log.debug("Request to get all AdminGroupUser");
        return adminFunctionRepository.findAll().stream().map(exitting -> modelMapper.map(exitting, AdminFuncDTO.class)).collect(Collectors.toList());
    }
}
