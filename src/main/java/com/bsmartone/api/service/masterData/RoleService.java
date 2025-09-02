/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bsmartone.api.service.masterData;

import com.bsmartone.api.model.masterData.Role;
import com.bsmartone.api.repository.masterData.RoleRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author jaime
 */
@Service
public class RoleService implements RoleServiceInterface {

    @Autowired
    private RoleRepository rolesRepository;

    @Override
    public Role create(Role role) {
        return rolesRepository.save(role);
    }

    @Override
    public List<Role> getAll() {
        return rolesRepository.findAll();
    }

    @Override
    public void delete(Role role) {
        rolesRepository.delete(role);
    }

    @Override
    public void deleteAll() {
        rolesRepository.deleteAll();
    }

    @Override
    public Optional<Role> findById(Long id) {
        return rolesRepository.findById(id);
    }

    public List<Role> getRolesByIds(Set<Role> roles) {
        Set<Long> roleIds=new HashSet<>();
        for(Role currentRole:roles)
        {
            roleIds.add(currentRole.getId());
        }
        return rolesRepository.findAllById(roleIds);
    }

    public List<Role> getAllActive() {
        return rolesRepository.findAllByDeletedFalse();
    }

    public Optional<Role> findByDescription(String user) {
        return rolesRepository.findByDescription(user);
    }
}
