package com.fis.invoice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegRoleAdminUserDTO extends BaseDTO {
    private List<TblUsersDTO> tblUsersDTOS;
    private Integer roleId;
    private Boolean success;

}
