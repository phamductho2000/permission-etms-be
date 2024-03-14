package com.fis.invoice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminRoleDTO {
    private BigInteger roleId;

    private String roleName;

    private String note;

    private BigInteger admin;

    private BigInteger qtct;
    private Date updatedDate;
}
