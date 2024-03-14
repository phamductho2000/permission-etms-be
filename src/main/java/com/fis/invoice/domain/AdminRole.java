package com.fis.invoice.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Entity
@Table(name = "ADMIN_ROLE")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminRole extends AbstractAuditingCreateEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "ROLE_ID")
    private Long roleId;

    @Column(name = "ROLE_NAME")
    private String roleName;

    @Column(name = "NOTE")
    private String note;

    @Column(name = "ADMIN")
    private BigInteger admin;

    @Column(name = "QTCT")
    private BigInteger qtct;
    
}
