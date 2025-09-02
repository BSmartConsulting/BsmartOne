/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bsmartone.api.dto.masterData;

import com.bsmartone.api.model.CatalogEntity;
import com.bsmartone.api.model.masterData.Role;
import com.bsmartone.api.repository.masterData.RoleRepository;
import com.bsmartone.api.util.BeanUtil;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class RoleDto extends CatalogEntity {

    private static RoleRepository roleRepository = BeanUtil.getBean(RoleRepository.class);
    private Long id;

    public static Role getRoleDto(Long statusid) {
        Optional<Role> currentRole = roleRepository.findById(statusid);
        Role result = null;
        if (currentRole.isPresent()) {
            result = currentRole.get();
        } else {
            currentRole = roleRepository.findByDescription("INACTIVO");
            if (currentRole.isPresent()) {
                result = currentRole.get();
            }
        }
        return result;
    }

    public static RoleDto RoleToRoleDto(Role record) {
        return RoleDto.builder()
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

    public static Role RoleDtoToRole(RoleDto recordDto) {
        return Role.builder()
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
    public RoleDto(Long id, String code, String description, LocalDateTime creationDate, LocalDateTime lastUpdatedDate, String createdBy, String lastUpdatedBy, Boolean deleted) {
        super(code, description, creationDate, lastUpdatedDate, createdBy, lastUpdatedBy, deleted);
        this.id = id;
    }

    public RoleDto() {
    }

}
