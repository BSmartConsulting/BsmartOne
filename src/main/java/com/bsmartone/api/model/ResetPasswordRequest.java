/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bsmartone.api.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.envers.Audited;

@Data
@Entity
@EqualsAndHashCode(callSuper = false)
@Table(name = "resetpasswordrequest")
@Audited
public class ResetPasswordRequest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "userid", nullable = false)
    private Long user;

    @Column(name = "confirmationcode", nullable = false)
    private String confirmationCode;

    @Column(name = "expirationdate", nullable = false)
    private LocalDateTime expirationDate;

    @Column(name = "isactive")
    private boolean isActive;

    public ResetPasswordRequest() {

    }

    @Builder
    public ResetPasswordRequest(Long id, Long user, String confirmationCode, LocalDateTime expirationDate, boolean isActive, LocalDateTime creationDate, LocalDateTime lastUpdatedDate, String createdBy, String lastUpdatedBy, Boolean deleted) {
        super(creationDate, lastUpdatedDate, createdBy, lastUpdatedBy, deleted);
        this.id = id;
        this.user = user;
        this.confirmationCode = confirmationCode;
        this.expirationDate = expirationDate;
        this.isActive = isActive;
    }

}
