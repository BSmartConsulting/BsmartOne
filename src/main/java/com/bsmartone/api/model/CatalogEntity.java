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
import org.hibernate.annotations.Comment;

@Getter
@Setter
@Accessors(chain = true)
@MappedSuperclass

public abstract class CatalogEntity extends BaseEntity {

    @Column(name = "code", unique = true, nullable = false)
    @Comment("Código")
    private String code;

    @Column(name = "description", nullable = false)
    @Comment("Descripción")
    private String description;

    public CatalogEntity(String code, String description, LocalDateTime creationDate, LocalDateTime lastUpdatedDate, String createdBy, String lastUpdatedBy, Boolean deleted) {
        super(creationDate, lastUpdatedDate, createdBy, lastUpdatedBy, deleted);
        this.code = code;
        this.description = description;
    }

    public CatalogEntity(LocalDateTime creationDate, LocalDateTime lastUpdatedDate, String createdBy, String lastUpdatedBy, Boolean deleted) {
        super(creationDate, lastUpdatedDate, createdBy, lastUpdatedBy, deleted);
    }

    public CatalogEntity() {
    }

}
