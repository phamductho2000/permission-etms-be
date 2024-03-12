package com.fis.invoice.service;

import com.fis.invoice.domain.TblUsers;
import com.fis.invoice.dto.TblUsersDTO;
import com.fis.invoice.repository.TblUsersRepository;
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
public class TblUsersService {
    private final Logger log = LoggerFactory.getLogger(TblUsersService.class);


    @Autowired
    private TblUsersRepository tblUsersRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Transactional (readOnly = true)
    public List<TblUsersDTO> findAll() throws Exception {
        log.debug("Request to get all TblUsers");
        return tblUsersRepository.findAll().stream().map(exitting -> modelMapper.map(exitting, TblUsersDTO.class)).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TblUsersDTO> findAllBySearch(String userName) throws Exception {
        log.debug("Request to get all findAllBySearch TblUsers");
        return tblUsersRepository.findAllByUsername(userName).stream().map(exitting -> modelMapper.map(exitting, TblUsersDTO.class)).collect(Collectors.toList());
    }

    @Transactional (readOnly = true)
    public TblUsersDTO updateTblUser(TblUsersDTO tblUsersDTO) throws Exception {
        log.debug("Request to update TblUsers : {}", tblUsersDTO);
        TblUsers tblUsers = new TblUsers();
        tblUsersRepository.save(tblUsers);
        return tblUsersDTO;
    }
}
