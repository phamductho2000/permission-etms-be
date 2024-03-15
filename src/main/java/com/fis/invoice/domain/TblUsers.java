package com.fis.invoice.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "TBL_USERS")
@AllArgsConstructor
@NoArgsConstructor
@IdClass(TblUserPk.class)
public class TblUsers {
    @Id
    @Column(name = "USER_ID")
    private Integer userId;

    @Column(name = "USERNAME")
    private String username;

    @Id
    @Column(name = "MA_CQT")
    private String maCqt;

    @Column(name = "CREATED")
    private Timestamp created;

    @Column(name = "UPDATED")
    private Timestamp updated;

//    @Column(name = "CREATED_BY")
//    private String createdBy;
//
//    @Column(name = "UPDATED_BY")
//    private String updatedBy;

}
