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
public class TblUserPk implements Serializable {
    @Id
    @Column(name="USER_ID")
    private Integer userId;

    @Id
    @Column(name = "MA_CQT")
    private String maCqt;
}
