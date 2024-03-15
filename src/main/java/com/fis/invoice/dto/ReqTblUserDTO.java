package com.fis.invoice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReqTblUserDTO extends BaseDTO {
    private List<TblUsersDTO> tblUsersDTOS;

    private Integer userId;
    private Boolean success;
}
