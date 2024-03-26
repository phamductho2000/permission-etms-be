package com.fis.invoice.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "Groupmembers")
@AllArgsConstructor
@NoArgsConstructor
@IdClass(GroupmembersPK.class)
public class Groupmembers {
    @Id
    @Column(name = "G_NAME")
    private String gName;
    
    @Id
    @Column(name = "G_MEMBER")
    private String gMember;
    
    @Column(name = "DOMAIN")
    private String domain;
    
    @Column(name = "TAXO")
    private String taxo;
    
}
