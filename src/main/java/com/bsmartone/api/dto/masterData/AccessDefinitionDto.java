/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bsmartone.api.dto.masterData;

import com.bsmartone.api.model.CatalogEntity;
import com.bsmartone.api.model.masterData.AccessDefinition;
import com.bsmartone.api.repository.masterData.AccessDefinitionRepository;
import com.bsmartone.api.util.BeanUtil;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class AccessDefinitionDto extends CatalogEntity {

    private static AccessDefinitionRepository AccessDefinitionRepository = BeanUtil.getBean(AccessDefinitionRepository.class);
    private Long id;

    public static AccessDefinition getAccessDefinitionDto(Long statusid) {
        Optional<AccessDefinition> currentAccessDefinition = AccessDefinitionRepository.findById(statusid);
        AccessDefinition result = null;
        if (currentAccessDefinition.isPresent()) {
            result = currentAccessDefinition.get();
        }
        return result;
    }

    public static AccessDefinitionDto AccessDefinitionToAccessDefinitionDto(AccessDefinition record) {
        return AccessDefinitionDto.builder()
                .id(record.getId())
                .code(record.getCode())
                .description(record.getDescription())
                .creationDate(record.getCreationDate())
                .lastUpdatedDate(record.getLastUpdatedDate())
                .createdBy(record.getCreatedBy())
                .lastUpdatedBy(record.getLastUpdatedBy())
                .deleted(record.getDeleted())
                .build();
    }

    public static AccessDefinition AccessDefinitionDtoToAccessDefinition(AccessDefinitionDto recordDto) {
        return AccessDefinition.builder()
                .id(recordDto.getId())
                .code(recordDto.getCode())
                .description(recordDto.getDescription())
                .creationDate(recordDto.getCreationDate())
                .lastUpdatedDate(recordDto.getLastUpdatedDate())
                .createdBy(recordDto.getCreatedBy())
                .lastUpdatedBy(recordDto.getLastUpdatedBy())
                .deleted(recordDto.getDeleted())
                .build();
    }

    @Builder
    public AccessDefinitionDto(Long id, String code, String description, LocalDateTime creationDate, LocalDateTime lastUpdatedDate, String createdBy, String lastUpdatedBy, Boolean deleted) {
        super(code, description, creationDate, lastUpdatedDate, createdBy, lastUpdatedBy, deleted);
        this.id = id;
    }

    public AccessDefinitionDto() {
    }

}
