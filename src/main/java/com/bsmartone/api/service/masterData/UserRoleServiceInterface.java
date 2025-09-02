/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.bsmartone.api.service.masterData;

import com.bsmartone.api.model.masterData.UserRole;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author jaime
 */
public interface UserRoleServiceInterface {

    UserRole create(UserRole userRole);

    void delete(UserRole userRole);

    void deleteAll();

    Optional<UserRole> findById(Long id);

    List<UserRole> getAll();

}
