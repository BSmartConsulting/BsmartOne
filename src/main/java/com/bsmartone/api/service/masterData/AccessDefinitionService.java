/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bsmartone.api.service.masterData;

import com.bsmartone.api.model.masterData.AccessDefinition;
import com.bsmartone.api.repository.masterData.AccessDefinitionRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author jaime
 */
@Service
public class AccessDefinitionService implements AccessDefinitionServiceInterface {

    @Autowired
    private AccessDefinitionRepository accessDefinitionRepository;

    @Override
    public AccessDefinition create(AccessDefinition accessDefinition) {
        return accessDefinitionRepository.save(accessDefinition);
        //return AccessDefinitionRepository.save(new AccessDefinition(AccessDefinition.getId(),AccessDefinition.getCodigo(),AccessDefinition.getDescripcion()));
    }

    @Override
    public List<AccessDefinition> getAll() {
        return accessDefinitionRepository.findAll();
    }

    @Override
    public void delete(AccessDefinition accessDefinition) {
        accessDefinitionRepository.delete(accessDefinition);
    }

    @Override
    public void deleteAll() {
        accessDefinitionRepository.deleteAll();
    }

    @Override
    public AccessDefinition findById(Long id) {
        return accessDefinitionRepository.findById(id).get();
    }

    public List<AccessDefinition> getAllActive() {
        return accessDefinitionRepository.findAllByDeletedFalse();
    }

}
