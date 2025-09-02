/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bsmartone.api.model.masterData;

import com.bsmartone.api.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

@Audited
@Getter
@Setter
@Data
@Entity
@EqualsAndHashCode(callSuper = false)
@Table(name = "userroles")
public class UserRole extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

   @ManyToOne(optional = false)
   @JoinColumn(name = "userid", nullable = false) // <-- columna FK en DB
   private User user;

   @ManyToOne(optional = false)
   @JoinColumn(name = "roleid", nullable = false) // <-- columna FK en DB
   private Role role;

    public UserRole() {

    }

    @Builder
    public UserRole(Long id, User user, Role role, LocalDateTime creationDate, LocalDateTime lastUpdatedDate, String createdBy, String lastUpdatedBy, Boolean deleted) {
        super(creationDate, lastUpdatedDate, createdBy, lastUpdatedBy, deleted);
        this.id = id;
        this.user = user;
        this.role = role;
    }

}
