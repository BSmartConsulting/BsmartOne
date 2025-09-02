/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bsmartone.api.service.masterData;

/**
 *
 * @author jaime
 */
import com.bsmartone.api.model.masterData.UserRole;
import com.bsmartone.api.repository.masterData.UserRoleRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author jaime
 */
@Service
public class UserRoleService implements UserRoleServiceInterface {

    @Autowired
    private UserRoleRepository userRolesRepository;

    @Override
    public UserRole create(UserRole userRole) {
        return userRolesRepository.save(userRole);
    }

    @Override
    public List<UserRole> getAll() {
        return userRolesRepository.findAll();
    }

    @Override
    public void delete(UserRole userRole) {
        userRolesRepository.delete(userRole);
    }

    @Override
    public void deleteAll() {
        userRolesRepository.deleteAll();
    }

    @Override
    public Optional<UserRole> findById(Long id) {
        return userRolesRepository.findById(id);
    }

    public void saveAll(List<UserRole> newUserRoles) {
        userRolesRepository.saveAll(newUserRoles);
    }

    public void deleteAll(List<UserRole> userRolesToRemove) {
        userRolesRepository.deleteAll(userRolesToRemove);
    }

    public Set<UserRole> findByUserId(Long id) {
        return userRolesRepository.findByUserId(id);
    }
}
