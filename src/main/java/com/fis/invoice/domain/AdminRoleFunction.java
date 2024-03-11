package com.fis.invoice.domain;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema = "ADMIN_ROLE_FUNC")
@IdClass(AdminRoleFunctionPk.class)
public class AdminRoleFunction extends AbstractAuditingCreateEntity {
    @Id
    @Column(name = "ROLE_ID")
    private Integer roleId;

    @Id
    @Column(name = "FUNC_ID")
    private Integer funcId;
}
