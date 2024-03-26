package com.fis.invoice.service;

import com.fis.invoice.domain.Groupmembers;
import com.fis.invoice.dto.GroupmembersDTO;
import com.fis.invoice.repository.GroupmembersRepository;
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
public class GroupmembersService {
    private final Logger log = LoggerFactory.getLogger(GroupmembersService.class);
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private GroupmembersRepository groupmembersRepository;


    @Transactional(readOnly = true)
    public List<GroupmembersDTO> findAllGroupmembers() throws Exception {
        log.debug("Request to get all Groupmembers");
        return groupmembersRepository.findAll().stream().map(exitting -> modelMapper.map(exitting, GroupmembersDTO.class)).collect(Collectors.toList());
    }

    // create Gruopmember
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public GroupmembersDTO createGroupmembers(GroupmembersDTO groupmembersDTO) throws Exception {
        log.debug("Request to creat groupmembersDTO");
        try {
            Groupmembers groupmembers = new Groupmembers();
            BeanUtils.copyProperties(groupmembersDTO, groupmembers);
            groupmembers = groupmembersRepository.save(groupmembers);
            BeanUtils.copyProperties(groupmembers, groupmembersDTO);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return groupmembersDTO;
    }
}
