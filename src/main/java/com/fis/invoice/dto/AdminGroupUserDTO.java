package com.fis.invoice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminGroupUserDTO {

    private BigInteger groupId;

    private BigInteger userId;

    private Timestamp created;

    private Timestamp updated;

    private String createdBy;

    private String updatedBy;
}
