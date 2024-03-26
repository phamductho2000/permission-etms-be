package com.fis.invoice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupmembersDTO {

    private String gName;


    private String gMember;

    private String domain;

    private String taxo;
}
