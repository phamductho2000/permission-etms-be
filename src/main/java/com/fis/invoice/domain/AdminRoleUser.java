package com.fis.invoice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.sql.Timestamp;


@Data
@Entity
@Table(name = "ADMIN_ROLE_USER")
@AllArgsConstructor
@NoArgsConstructor
public class AdminRoleUser {
    @Id

    @Column(name = "ROLE_ID")
    private BigInteger roleId;

    @Column(name = "USER_ID")
    private BigInteger userId;

    @Column(name = "CREATED")
    private Timestamp created;

    @Column(name = "UPDATED")
    private Timestamp updated;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "UPDATED_BY")
    private String updatedBy;
}
