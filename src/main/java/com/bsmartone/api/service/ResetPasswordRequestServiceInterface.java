/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.bsmartone.api.service;

import com.bsmartone.api.model.ResetPasswordRequest;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author jaime
 */
public interface ResetPasswordRequestServiceInterface {

    ResetPasswordRequest create(ResetPasswordRequest resetPasswordRequest);

    void delete(ResetPasswordRequest resetPasswordRequest);

    void deleteAll();

    Optional<ResetPasswordRequest> findById(Long id);

    List<ResetPasswordRequest> getAll();

}
