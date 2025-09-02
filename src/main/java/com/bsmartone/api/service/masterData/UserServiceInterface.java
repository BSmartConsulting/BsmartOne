/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.bsmartone.api.service.masterData;

import com.bsmartone.api.model.masterData.User;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author jaime
 */
public interface UserServiceInterface {

    User create(User user, String operacion);

    void delete(User user);

    void deleteAll();

    Optional<User> findById(Long id);

    List<User> getAll();

}
