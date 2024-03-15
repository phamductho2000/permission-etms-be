package com.fis.invoice.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Entity
@Table(name = "USER_ROLE")
@AllArgsConstructor
@NoArgsConstructor
public class UserRole extends AbstractAuditingCreateEntity {
    @Id
    @Column(name = "USER_ID")
    private Integer userId;

    @Column(name = "USERNAME")
    private String username;

    @Column(name = "DESCR")
    private String descr;

    @Column(name = "AREA_CODE")
    private String areaCode;

    @Column(name = "AREA_TYPE")
    private String areaType;

//    @Column(name = "CREATED")
//    private Timestamp created;
//
//    @Column(name = "UPDATED")
//    private Timestamp updated;
//
////    @Column(name = "CREATED_BY")
////    private String createdBy;
//
//    @Column(name = "CREATED_DATE")
//    private String createdDate;

}
