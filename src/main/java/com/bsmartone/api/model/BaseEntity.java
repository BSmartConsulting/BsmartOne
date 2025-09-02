/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bsmartone.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.hibernate.annotations.Comment;
import jakarta.persistence.EntityListeners;

/**
 *
 * @author jaime
 */
@Getter
@Setter
@Accessors(chain = true)
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @Column(name = "creationdate")
    @Comment("Fecha de creación")
    @JsonSerialize(using = ToStringSerializer.class)
    private LocalDateTime creationDate;

    @Column(name = "lastupdateddate")
    @JsonSerialize(using = ToStringSerializer.class)
    @Comment("Fecha de actualizacion")
    private LocalDateTime lastUpdatedDate;

    @Column(name = "createdby")
    @Comment("Usuario de creación")
    private String createdBy;

    @Column(name = "lastupdatedby")
    @Comment("Usuario de actualizacion")
    private String lastUpdatedBy;

    @Column(name = "deleted")
    @Comment("valida registro eliminado o no")
    private Boolean deleted;

    public BaseEntity(LocalDateTime creationDate, LocalDateTime lastUpdatedDate, String createdBy, String lastUpdatedBy, Boolean deleted) {
        this.creationDate = creationDate;
        this.lastUpdatedDate = lastUpdatedDate;
        this.createdBy = createdBy;
        this.lastUpdatedBy = lastUpdatedBy;
        this.deleted = deleted;
    }

    public BaseEntity() {
    }

}
