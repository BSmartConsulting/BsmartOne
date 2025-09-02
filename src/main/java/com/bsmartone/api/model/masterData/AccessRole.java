/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bsmartone.api.model.masterData;

import com.bsmartone.api.model.BaseEntity;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.envers.Audited;

@Audited
@Data
@Entity
@EqualsAndHashCode(callSuper = false)
@Table(name = "accessrole")
public class AccessRole extends BaseEntity implements Serializable {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long role;

    @Column(nullable = false)
    private Long accessdefinition;


    public AccessRole() {
    }

    @Builder
    public AccessRole(Long id, Long role, Long accessDefinition, LocalDateTime creationDate, LocalDateTime lastUpdatedDate, String createdBy, String lastUpdatedBy, Boolean deleted) {
        super(creationDate, lastUpdatedDate, createdBy, lastUpdatedBy, deleted);
        this.id = id;
        this.role = role;
        this.accessdefinition = accessDefinition;
    }

}
