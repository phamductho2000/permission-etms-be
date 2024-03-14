package com.fis.invoice.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminRoleUserDTO {
    private BigInteger roleId;

    private BigInteger userId;

//    private Timestamp created;

    private Timestamp updated;

//    private String createdBy;
//
//    private String updatedBy;
}
