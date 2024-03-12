package com.fis.invoice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminRoleFunctionPk implements Serializable {
    @Id
    @Column(name="ROLE_ID")
    private Integer roleId;

    @Id
    @Column(name="FUNC_ID")
    private Integer funcId;
}
