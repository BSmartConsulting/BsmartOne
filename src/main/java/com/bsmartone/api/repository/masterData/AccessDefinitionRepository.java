/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.bsmartone.api.repository.masterData;

import com.bsmartone.api.model.masterData.AccessDefinition;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccessDefinitionRepository extends JpaRepository<AccessDefinition, Long> {

    public List<AccessDefinition> findAllByDeletedFalse();

}
