package com.fis.invoice.service;

import com.fis.invoice.domain.AdminFunction;
import com.fis.invoice.domain.AdminRoleFunction;
import com.fis.invoice.dto.AdminRoleFunctionDTO;
import com.fis.invoice.dto.ReqRoleAdminFuncDTO;
import com.fis.invoice.repository.AdminFunctionRepository;
import com.fis.invoice.repository.AdminRoleFunctionRepository;
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
public class AdminRoleFunctionService {
    private final Logger log = LoggerFactory.getLogger(AdminRoleFunctionService.class);
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AdminRoleFunctionRepository adminRoleFunctionRepository;

    @Autowired
    private AdminFunctionRepository adminFunctionRepository;

    @Transactional(readOnly = true)
    public List<AdminRoleFunctionDTO> findAll() throws Exception {
        log.debug("Request to get all adminRoleFunction");
        return adminRoleFunctionRepository.findAll().stream().map(exitting -> modelMapper.map(exitting, AdminRoleFunctionDTO.class)).collect(Collectors.toList());
    }

    // tạo mới
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public AdminRoleFunctionDTO create(AdminRoleFunctionDTO adminRoleFunctionDTO) throws Exception {
        log.debug("Request to creat adminRoleFunction");
        try {
            AdminRoleFunction adminRoleFunction = new AdminRoleFunction();
            BeanUtils.copyProperties(adminRoleFunctionDTO, adminRoleFunction);
            adminRoleFunction = adminRoleFunctionRepository.save(adminRoleFunction);
            BeanUtils.copyProperties(adminRoleFunction, adminRoleFunctionDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return adminRoleFunctionDTO;
    }

    // Update
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public ReqRoleAdminFuncDTO updateFunctionToRole(ReqRoleAdminFuncDTO req) throws Exception {
        log.debug("Request to update adminRoleFunction");
        try {
            // find all function by role id
            List<AdminRoleFunction> adminRoleFunctionList = adminRoleFunctionRepository.findAllByRoleId(req.getRoleId());
            if (!adminRoleFunctionList.isEmpty()) {
                // remove all
                adminRoleFunctionRepository.deleteAll(adminRoleFunctionList);
            }

            // insert new
            List<Integer> funcIds = req.getAdminFuncDTOList().stream().map(f -> f.getFuncId()).collect(Collectors.toList());
            if (!funcIds.isEmpty()) {
                List<AdminFunction> adminFunctions = adminFunctionRepository.findAllById(funcIds);
                if (!adminFunctions.isEmpty()) {
                    adminFunctions.forEach(func -> {
                        AdminRoleFunction adminRoleFunction = new AdminRoleFunction();
                        adminRoleFunction.setRoleId(req.getRoleId());
                        adminRoleFunction.setFuncId(func.getFuncId());
                        adminRoleFunctionRepository.save(adminRoleFunction);
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return req;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public List<AdminRoleFunctionDTO> findAllUserByGroupId(AdminRoleFunctionDTO adminRoleFunctionDTO) throws Exception {
        log.debug("Request to get findAllByUserId");

        return adminRoleFunctionRepository.findAllRoleFunctionGroupId(adminRoleFunctionDTO.getRoleId()).stream()
                .map(existing -> modelMapper.map(existing, AdminRoleFunctionDTO.class))
                .collect(Collectors.toList());
    }
}
