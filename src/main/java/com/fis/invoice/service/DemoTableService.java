package com.fis.invoice.service;

import com.fis.invoice.dto.DemoTableDTO;
import com.fis.invoice.repository.DemoTableRepository;
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
public class DemoTableService {
    private final Logger log = LoggerFactory.getLogger(DemoTableService.class);


    @Autowired
    private DemoTableRepository demoTableRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Transactional (readOnly = true)
    public List<DemoTableDTO> findAll() throws Exception {
        log.debug("Request to get all DemoTable");
        return demoTableRepository.findAll().stream().map(exitting -> modelMapper.map(exitting, DemoTableDTO.class)).collect(Collectors.toList());
    }

}
