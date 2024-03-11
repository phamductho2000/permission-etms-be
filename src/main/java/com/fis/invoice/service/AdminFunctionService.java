package com.fis.invoice.service;

import com.fis.invoice.domain.AdminFunction;
import com.fis.invoice.dto.AdminFuncDTO;
import com.fis.invoice.repository.AdminFunctionRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminFunctionService {

    @Autowired
    private AdminFunctionRepository adminFunctionRepository;

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
}
