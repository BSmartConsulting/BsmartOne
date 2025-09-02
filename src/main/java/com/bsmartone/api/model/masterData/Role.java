/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bsmartone.api.model.masterData;

import com.bsmartone.api.model.CatalogEntity;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.envers.Audited;

@Audited
@Entity
@Table(name = "roles")
@Data
@EqualsAndHashCode(callSuper = false)
public class Role extends CatalogEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder
    public Role(Long id, String code, String description, LocalDateTime creationDate, LocalDateTime lastUpdatedDate, String createdBy, String lastUpdatedBy, Boolean deleted) {
        super(code, description, creationDate, lastUpdatedDate, createdBy, lastUpdatedBy, deleted);
        this.id = id;
    }

    public Role() {
    }

}
