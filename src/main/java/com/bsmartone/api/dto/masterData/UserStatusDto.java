/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bsmartone.api.dto.masterData;

import com.bsmartone.api.model.CatalogEntity;
import com.bsmartone.api.model.masterData.UserStatus;
import com.bsmartone.api.repository.masterData.UserStatusRepository;
import com.bsmartone.api.util.BeanUtil;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class UserStatusDto extends CatalogEntity {

    private static UserStatusRepository userStatusRepository = BeanUtil.getBean(UserStatusRepository.class);
    private Long id;

    public static UserStatus getUserStatusDto(Long statusid) {
        Optional<UserStatus> currentUserStatus = userStatusRepository.findById(statusid);
        UserStatus result = null;
        if (currentUserStatus.isPresent()) {
            result = currentUserStatus.get();
        } else {
            currentUserStatus = userStatusRepository.findByDescription("INACTIVO");
            if (currentUserStatus.isPresent()) {
                result = currentUserStatus.get();
            }
        }
        return result;
    }

    public static UserStatusDto UserStatusToUserStatusDto(UserStatus record) {
        return UserStatusDto.builder()
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

    public static UserStatus UserStatusDtoToUserStatus(UserStatusDto recordDto) {
        return UserStatus.builder()
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
    public UserStatusDto(Long id, String code, String description, LocalDateTime creationDate, LocalDateTime lastUpdatedDate, String createdBy, String lastUpdatedBy, Boolean deleted) {
        super(code, description, creationDate, lastUpdatedDate, createdBy, lastUpdatedBy, deleted);
        this.id = id;
    }

    public UserStatusDto() {
    }

}
