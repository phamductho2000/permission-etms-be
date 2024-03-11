package com.fis.invoice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminFuncDTO extends BaseDTO {

    private Integer funcId;

    private String funcName;

    private String funcCode;
}
