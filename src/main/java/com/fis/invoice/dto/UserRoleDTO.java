package com.fis.invoice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRoleDTO {
    private BigInteger userId;
    private String username;
    private String descr;
    private String areaCode;
    private String areaType;
    private Timestamp created;
    private Timestamp updated;
    private String createdBy;
    private String createdDate;
}
