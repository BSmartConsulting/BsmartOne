/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.bsmartone.api.service.masterData;

import com.bsmartone.api.model.masterData.AccessRole;
import java.util.List;

/**
 *
 * @author jaime
 */
public interface AccessRoleServiceInterface {

    AccessRole create(AccessRole accessRole);

    void delete(AccessRole accessRole);

    void deleteAll();

    AccessRole findById(Long id);

    List<AccessRole> getAll();

    List<AccessRole> getAllActive();
}
