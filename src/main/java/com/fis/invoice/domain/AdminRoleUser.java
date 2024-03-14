package com.fis.invoice.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Entity
@Table(name = "ADMIN_ROLE_USER")
@AllArgsConstructor
@NoArgsConstructor
@IdClass(AdminRoleUserPk.class)
public class AdminRoleUser extends AbstractAuditingCreateEntity {
    @Id

    @Column(name = "ROLE_ID")
    private Integer roleId;
    @Id
    @Column(name = "USER_ID")
    private Integer userId;

//    @Column(name = "CREATED")
//    private Timestamp created;
//
//    @Column(name = "UPDATED")
//    private Timestamp updated;
//
//    @Column(name = "CREATED_BY")
//    private String createdBy;
//
//    @Column(name = "UPDATED_BY")
//    private String updatedBy;
}
