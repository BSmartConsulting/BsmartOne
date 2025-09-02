/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.bsmartone.api.repository;

import com.bsmartone.api.model.ResetPasswordRequest;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResetPasswordRequestRepository extends JpaRepository<ResetPasswordRequest, Long> {

    ResetPasswordRequest findByConfirmationCode(String confirmationCode);

    public List<ResetPasswordRequest> findAllByDeletedFalse();
}
