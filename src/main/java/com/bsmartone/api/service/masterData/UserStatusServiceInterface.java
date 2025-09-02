/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.bsmartone.api.service.masterData;

import com.bsmartone.api.model.masterData.UserStatus;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author jaime
 */
public interface UserStatusServiceInterface {

    UserStatus create(UserStatus userStatus);

    void delete(UserStatus userStatus);

    void deleteAll();

    Optional<UserStatus> findById(Long id);

    List<UserStatus> getAll();

}
