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
@Entity
@Table(name = "cities")
@Data
@EqualsAndHashCode(callSuper = false)
public class City extends BaseEntity implements Serializable {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, name = "code")
    private String code;

    @Column(name = "city")
    private String city;

    @Column(name = "department")
    private String department;

    public City() {
    }

    @Builder
    public City(Long id, String code, String city, String department, LocalDateTime creationDate, LocalDateTime lastUpdatedDate, String createdBy, String lastUpdatedBy, Boolean deleted) {
        super(creationDate, lastUpdatedDate, createdBy, lastUpdatedBy, deleted);
        this.id = id;
        this.code = code;
        this.city = city;
        this.department = department;
    }

}
