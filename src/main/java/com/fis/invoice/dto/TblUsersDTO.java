package com.fis.invoice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TblUsersDTO {
    private BigInteger userId;

    private String username;

    private String maCqt;

    private Timestamp created;

    private Timestamp updated;

    private String createdBy;

    private String updatedBy;

    private String quickSearch;
}
