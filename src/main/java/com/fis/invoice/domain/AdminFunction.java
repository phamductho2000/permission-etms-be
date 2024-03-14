package com.fis.invoice.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ADMIN_FUNCTION")
@Entity
public class AdminFunction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FUNC_ID")
    private Integer funcId;

    @Column(name = "FUNC_NAME")
    private String funcName;

    @Column(name = "FUNC_CODE")
    private String funcCode;
}
