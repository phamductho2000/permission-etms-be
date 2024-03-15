package com.fis.invoice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReqRoleAdminFuncDTO extends BaseDTO {

    private List<AdminFuncDTO> adminFuncDTOList;

    private Integer roleId;
    private Boolean success;
}
