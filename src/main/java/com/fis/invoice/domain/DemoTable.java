package com.fis.invoice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Data
@Entity
@Table(name = "DEMO_TABLE")
@AllArgsConstructor
@NoArgsConstructor
public class DemoTable {

    @Id
    @Column(name = "ID")
    private BigInteger id;

    @Column(name = "PASSWORD")
    private String password;

}
