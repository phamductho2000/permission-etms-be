package com.fis.invoice.service;

import com.fis.invoice.domain.ZtbMapCqt;
import com.fis.invoice.dto.ZtbMapCqtDTO;
import com.fis.invoice.repository.ZtbMapCqtRepository;
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
public class ZtbMapCqtService {
    private final Logger log = LoggerFactory.getLogger(ZtbMapCqtService.class);


    @Autowired
    private ZtbMapCqtRepository ztbMapCqtRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public List<ZtbMapCqtDTO> findAll() throws Exception {
        log.debug("Request to get all ZtbMapCqtDTO");
        return ztbMapCqtRepository.findAll().stream().map(exitting -> modelMapper.map(exitting, ZtbMapCqtDTO.class)).collect(Collectors.toList());
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public ZtbMapCqtDTO createZtbMapCqt(ZtbMapCqtDTO ztbMapCqtDTO) throws Exception {
        log.debug("Request to creat userRoleDTO");
        try {
            ZtbMapCqt ztbMapCqt = new ZtbMapCqt();
            BeanUtils.copyProperties(ztbMapCqtDTO, ztbMapCqt);
            ztbMapCqt = ztbMapCqtRepository.save(ztbMapCqt);
            BeanUtils.copyProperties(ztbMapCqt, ztbMapCqtDTO);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return ztbMapCqtDTO;
    }

}
