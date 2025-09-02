/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.bsmartone.api.service.masterData;

import com.bsmartone.api.model.masterData.Role;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author jaime
 */
public interface RoleServiceInterface {

    Role create(Role role);

    void delete(Role role);

    void deleteAll();

    Optional<Role> findById(Long id);

    List<Role> getAll();

}
