/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bsmartone.api.service.masterData;

import com.bsmartone.api.model.masterData.AccessRole;
import com.bsmartone.api.repository.masterData.AccessRoleRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author jaime
 */
@Service
public class AccessRoleService implements AccessRoleServiceInterface {

    @Autowired
    private AccessRoleRepository accessRoleRepository;

    @Override
    public AccessRole create(AccessRole accessRole) {
        return accessRoleRepository.save(accessRole);
        //return AccessRoleRepository.save(new AccessRole(accessRole.getId(),accessRole.getCodigo(),accessRole.getDescripcion()));
    }

    @Override
    public List<AccessRole> getAll() {
        return accessRoleRepository.findAll();
    }

    @Override
    public void delete(AccessRole accessRole) {
        accessRoleRepository.delete(accessRole);
    }

    @Override
    public void deleteAll() {
        accessRoleRepository.deleteAll();
    }

    @Override
    public AccessRole findById(Long id) {
        return accessRoleRepository.findById(id).get();
    }

    @Override
    public List<AccessRole> getAllActive() {
        return accessRoleRepository.findAllByDeletedFalse();
    }
}
