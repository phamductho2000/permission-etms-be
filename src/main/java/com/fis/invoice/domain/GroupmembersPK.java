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
public class GroupmembersPK implements Serializable {
    @Id
    @Column(name="G_MEMBER")
    private String gMember;

    @Id
    @Column(name="G_NAME")
    private String gName;
}
