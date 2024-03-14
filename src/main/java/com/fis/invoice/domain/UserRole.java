package com.fis.invoice.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Objects;

@Data
@Entity
@Table(name = "USER_ROLE")
@AllArgsConstructor
@NoArgsConstructor
public class UserRole {
    @Id
    @Column(name = "USER_ID")
    private BigInteger userId;

    @Column(name = "USERNAME")
    private String username;

    @Column(name = "DESCR")
    private String descr;

    @Column(name = "AREA_CODE")
    private String areaCode;

    @Column(name = "AREA_TYPE")
    private String areaType;

    @Column(name = "CREATED")
    private Timestamp created;

    @Column(name = "UPDATED")
    private Timestamp updated;

//    @Column(name = "CREATED_BY")
//    private String createdBy;

    @Column(name = "CREATED_DATE")
    private String createdDate;
}
