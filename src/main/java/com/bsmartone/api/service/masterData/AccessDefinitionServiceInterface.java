/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.bsmartone.api.service.masterData;

import com.bsmartone.api.model.masterData.AccessDefinition;
import java.util.List;

/**
 *
 * @author jaime
 */
public interface AccessDefinitionServiceInterface {

    AccessDefinition create(AccessDefinition accessDefinition);

    void delete(AccessDefinition accessDefinition);

    void deleteAll();

    AccessDefinition findById(Long id);

    List<AccessDefinition> getAll();

}
