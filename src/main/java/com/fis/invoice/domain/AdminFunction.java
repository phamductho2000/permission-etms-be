package com.fis.invoice.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(schema = "ADMIN_FUNCTION")
@Entity
public class AdminFunction extends AbstractAuditingCreateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FUNC_ID")
    private Integer funcId;

    @Column(name = "FUNC_NAME")
    private String funcName;

    @Column(name = "FUNC_CODE")
    private String funcCode;
}
