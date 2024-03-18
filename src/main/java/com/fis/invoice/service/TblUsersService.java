package com.fis.invoice.service;

import com.fis.invoice.domain.AdminRoleUser;
import com.fis.invoice.domain.TblUsers;
import com.fis.invoice.domain.ZtbMapCqt;
import com.fis.invoice.dto.AdminRoleUserDTO;
import com.fis.invoice.dto.ReqTblUserDTO;
import com.fis.invoice.dto.TblUsersDTO;
import com.fis.invoice.repository.TblUsersRepository;
import com.fis.invoice.repository.ZtbMapCqtRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TblUsersService {
    private final Logger log = LoggerFactory.getLogger(TblUsersService.class);


    @Autowired
    private TblUsersRepository tblUsersRepository;
    @Autowired
    private ZtbMapCqtRepository ztbMapCqtRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public List<TblUsersDTO> findAll() throws Exception {
        log.debug("Request to get all TblUsers");
        return tblUsersRepository.findAll().stream()
                .map(exitting -> {
                    TblUsersDTO dto = new TblUsersDTO();
                    // check data trong db có trường nào bị null ko
                    if (exitting != null)
                        dto = modelMapper.map(exitting, TblUsersDTO.class);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public List<TblUsersDTO> findAllBySearch(String userName) throws Exception {
        log.debug("Request to get all findAllBySearch TblUsers");
        return
                userName.equals("") ? (tblUsersRepository.findAll().stream()
                        .map(exitting -> {
                            TblUsersDTO dto = new TblUsersDTO();
                            // check data trong db có trường nào bị null ko
                            if (exitting != null)
                                dto = modelMapper.map(exitting, TblUsersDTO.class);
                            return dto;
                        })
                        .collect(Collectors.toList()))
                : (tblUsersRepository.findAllByUsername(userName).stream().map(exitting -> modelMapper.map(exitting, TblUsersDTO.class)).collect(Collectors.toList()));
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public ReqTblUserDTO updateTblUser(ReqTblUserDTO req) throws Exception {
        log.debug("Request to update updateTblUser");
        try {
            // find all function by role id
            List<TblUsers> tblUsersDTOS = tblUsersRepository.findAllByUserIdIs(req.getUserId());
            if (!tblUsersDTOS.isEmpty()) {
                // remove all
                tblUsersRepository.deleteAll(tblUsersDTOS);
            }

            // insert new theo UserId và mcqthue
            List<String> maCQT = req.getTblUsersDTOS().stream().map(f -> f.getMaCqt()).collect(Collectors.toList());
            if (!maCQT.isEmpty()) {
                List<ZtbMapCqt> ztbMapCqts = ztbMapCqtRepository.findAllById(maCQT);
                if (!ztbMapCqts.isEmpty()) {
                    ztbMapCqts.forEach(func -> {
                        TblUsers tblUsers = new TblUsers();
                        tblUsers.setUserId(req.getUserId());
                        tblUsers.setMaCqt(String.valueOf(func.getMaCqt()));
                        tblUsersRepository.save(tblUsers);
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return req;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public List<TblUsersDTO> findAllTblUserGroupId(TblUsersDTO tblUsersDTO) throws Exception {
        log.debug("Request to get findAllTblUserGroupId");

        return tblUsersRepository.findAllTblUserGroupId(tblUsersDTO.getUserId()).stream()
                .map(existing -> modelMapper.map(existing, TblUsersDTO.class))
                .collect(Collectors.toList());
    }
}
