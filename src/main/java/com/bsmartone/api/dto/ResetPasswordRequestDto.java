/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bsmartone.api.dto;

import com.bsmartone.api.dto.masterData.UserDto;
import com.bsmartone.api.model.*;
import com.bsmartone.api.model.masterData.User;
import com.bsmartone.api.repository.masterData.RoleRepository;
import com.bsmartone.api.repository.masterData.UserRepository;
import com.bsmartone.api.repository.masterData.UserStatusRepository;
import com.bsmartone.api.util.BeanUtil;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ResetPasswordRequestDto extends BaseEntity {

    private static UserRepository userRepository = BeanUtil.getBean(UserRepository.class);
    private static UserStatusRepository userStatusRepository = BeanUtil.getBean(UserStatusRepository.class);
    private static RoleRepository roleRepository = BeanUtil.getBean(RoleRepository.class);

    private Long id;
    private UserDto user;
    private String confirmationCode;
    private LocalDateTime expirationDate;
    private boolean isActive;

    public static ResetPasswordRequest ResetPasswordRequestDtoToResetPasswordRequest(ResetPasswordRequestDto resetPasswordRequestdto) {
        ResetPasswordRequest resetPasswordRequest = ResetPasswordRequest.builder()
                .id(resetPasswordRequestdto.getId())
                .confirmationCode((resetPasswordRequestdto.getConfirmationCode()))
                .user(resetPasswordRequestdto.getUser().getId())
                .expirationDate(resetPasswordRequestdto.getExpirationDate())
                .isActive(resetPasswordRequestdto.isActive())
                .deleted(resetPasswordRequestdto.getDeleted())
                .lastUpdatedBy(resetPasswordRequestdto.getLastUpdatedBy())
                .lastUpdatedDate(resetPasswordRequestdto.getLastUpdatedDate())
                .creationDate(resetPasswordRequestdto.getCreationDate())
                .createdBy(resetPasswordRequestdto.getCreatedBy())
                .build();
        return resetPasswordRequest;
    }

    public static ResetPasswordRequestDto ResetPasswordRequestToResetPasswordRequestDto(ResetPasswordRequest resetPasswordRequest) {
        List<User> luser = userRepository.findAllByDeletedFalse();
        List<UserDto> luserDto = new ArrayList<>();
        for (User u : luser) {
            luserDto.add(UserDto.UserToUserDto(u));
        }
        Map<Long, UserDto> userMap = luserDto.stream()
                .collect(Collectors.toMap(UserDto::getId, Function.identity()));
        UserDto userDto = luserDto.stream()
                .filter(user -> user.getId().equals(resetPasswordRequest.getUser()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No se encontró el usuario con el taxId especificado"));

        ResetPasswordRequestDto resetPasswordRequestdto = ResetPasswordRequestDto.builder()
                .id(resetPasswordRequest.getId())
                .user(userDto)
                .confirmationCode(resetPasswordRequest.getConfirmationCode())
                .expirationDate(resetPasswordRequest.getExpirationDate())
                .isActive(resetPasswordRequest.isActive())
                .deleted(resetPasswordRequest.getDeleted())
                .lastUpdatedBy(resetPasswordRequest.getLastUpdatedBy())
                .lastUpdatedDate(resetPasswordRequest.getLastUpdatedDate())
                .creationDate(resetPasswordRequest.getCreationDate())
                .createdBy(resetPasswordRequest.getCreatedBy())
                .build();
        return resetPasswordRequestdto;
    }

    public ResetPasswordRequestDto() {
    }

    @Builder
    public ResetPasswordRequestDto(Long id, UserDto user, String confirmationCode, LocalDateTime expirationDate, boolean isActive, LocalDateTime creationDate, LocalDateTime lastUpdatedDate, String createdBy, String lastUpdatedBy, Boolean deleted) {
        super(creationDate, lastUpdatedDate, createdBy, lastUpdatedBy, deleted);
        this.id = id;
        this.user = user;
        this.confirmationCode = confirmationCode;
        this.expirationDate = expirationDate;
        this.isActive = isActive;
    }

}
