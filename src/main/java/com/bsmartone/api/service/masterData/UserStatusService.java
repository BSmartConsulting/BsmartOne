/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bsmartone.api.service.masterData;

import com.bsmartone.api.model.masterData.UserStatus;
import com.bsmartone.api.repository.masterData.UserStatusRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author jaime
 */
@Service
public class UserStatusService implements UserStatusServiceInterface {

    @Autowired
    private UserStatusRepository userStatusRepository;

    @Override
    public UserStatus create(UserStatus userStatus) {
        return userStatusRepository.save(userStatus);
    }

    @Override
    public List<UserStatus> getAll() {
        return userStatusRepository.findAll();
    }

    @Override
    public void delete(UserStatus userStatus) {
        userStatusRepository.delete(userStatus);
    }

    @Override
    public void deleteAll() {
        userStatusRepository.deleteAll();
    }

    @Override
    public Optional<UserStatus> findById(Long id) {
        return userStatusRepository.findById(id);
    }

    public List<UserStatus> getAllActive() {
        return userStatusRepository.findAllByDeletedFalse();
    }

    public Optional<UserStatus> findByDescription(String bloqueado) {
        return userStatusRepository.findByDescription(bloqueado);
    }
}
