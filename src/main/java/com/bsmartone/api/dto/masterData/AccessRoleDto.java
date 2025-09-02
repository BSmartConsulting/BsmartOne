/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bsmartone.api.dto.masterData;

import com.bsmartone.api.model.*;
import com.bsmartone.api.model.masterData.AccessRole;
import com.bsmartone.api.model.masterData.AccessDefinition;
import com.bsmartone.api.model.masterData.Role;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class AccessRoleDto extends BaseEntity implements Serializable {

    private Long id;
    private AccessDefinitionDto accessdefinition;
    private RoleDto role;


    public static AccessRole AccessRoleDtoToAccessRole(AccessRoleDto recordDto) {
        AccessDefinition accessDefinition = AccessDefinitionDto.AccessDefinitionDtoToAccessDefinition(recordDto.getAccessdefinition());
        Role role = RoleDto.RoleDtoToRole(recordDto.getRole());

        AccessRole accessrole = AccessRole.builder()
                .id(recordDto.getId())
                .accessDefinition(accessDefinition.getId())
                .role(role.getId())
                .creationDate(recordDto.getCreationDate())
                .lastUpdatedDate(recordDto.getLastUpdatedDate())
                .createdBy(recordDto.getCreatedBy())
                .lastUpdatedBy(recordDto.getLastUpdatedBy())
                .deleted(recordDto.getDeleted())
                .build();
        return accessrole;
    }

    public static AccessRoleDto AccessRoleToAccessRoleDto(AccessRole record) {
        AccessDefinitionDto accessDefinitionDto = AccessDefinitionDto.AccessDefinitionToAccessDefinitionDto(AccessDefinitionDto.getAccessDefinitionDto(record.getAccessdefinition()));
        RoleDto roleDto = RoleDto.RoleToRoleDto(RoleDto.getRoleDto(record.getRole()));
        AccessRoleDto accessroledto = AccessRoleDto.builder()
                .id(record.getId())
                .accessdefinition(accessDefinitionDto)
                .role(roleDto)
                .lastUpdatedDate(record.getLastUpdatedDate())
                .createdBy(record.getCreatedBy())
                .lastUpdatedBy(record.getLastUpdatedBy())
                .deleted(record.getDeleted())
                .build();
        return accessroledto;
    }

    public AccessRoleDto() {
    }

    @Builder
    public AccessRoleDto(Long id, AccessDefinitionDto accessdefinition, RoleDto role, LocalDateTime creationDate, LocalDateTime lastUpdatedDate, String createdBy, String lastUpdatedBy, Boolean deleted) {
        super(creationDate, lastUpdatedDate, createdBy, lastUpdatedBy, deleted);
        this.id = id;
        this.accessdefinition = accessdefinition;
        this.role = role;
    }

}
