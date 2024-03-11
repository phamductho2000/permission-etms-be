package com.fis.invoice.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.util.Date;

import static com.fis.invoice.service.Utils.getCurrentUser;

/**
 * Base abstract class for entities which will hold definitions for created, last modified, created by,
 * last modified by attributes.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractAuditingCreateEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @CreatedBy
    @Column(name = "CREATED_BY", nullable = false, length = 40, updatable = false)
    @JsonIgnore
    private String nguoiTao = getCurrentUser().getName();

    @CreatedDate
    @Column(name = "CREATED", updatable = false)
    @JsonIgnore
    private Date ngayTao = new Date(System.currentTimeMillis());
    
    @LastModifiedBy
    @Column(name="UPDATED_BY")
    @JsonIgnore
	private String nguoiSua;
    
    @LastModifiedDate
	@Column(name="UPDATED")
    @JsonIgnore
	private Date ngaySua = new Date(System.currentTimeMillis());
}
