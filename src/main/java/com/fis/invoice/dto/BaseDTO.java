package com.fis.invoice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseDTO {

    private String createdBy;

    private Date createdDate;

    private String updatedBy;

    private Date updatedDate;
}
