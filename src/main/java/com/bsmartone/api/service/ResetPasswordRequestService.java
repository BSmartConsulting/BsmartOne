/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bsmartone.api.service;

import com.bsmartone.api.dto.GenericResponse;
import com.bsmartone.api.dto.ResetPasswordRequestDto;
import com.bsmartone.api.dto.masterData.UserDto;
import com.bsmartone.api.model.ResetPasswordRequest;
import com.bsmartone.api.model.masterData.UserStatus;
import com.bsmartone.api.repository.ResetPasswordRequestRepository;
import com.bsmartone.api.repository.masterData.UserRepository;
import com.bsmartone.api.service.masterData.RoleService;
import com.bsmartone.api.service.masterData.UserService;
import com.bsmartone.api.service.masterData.UserStatusService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * @author jaime
 */
@Service
public class ResetPasswordRequestService implements ResetPasswordRequestServiceInterface {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private ResetPasswordRequestRepository resetPasswordRequestsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserStatusService userStatusService;

    @Autowired
    private RoleService roleService;

    @Override
    public ResetPasswordRequest create(ResetPasswordRequest resetPasswordRequest) {
        return resetPasswordRequestsRepository.save(resetPasswordRequest);
    }

    @Override
    public List<ResetPasswordRequest> getAll() {
        return resetPasswordRequestsRepository.findAll();
    }

    @Override
    public void delete(ResetPasswordRequest resetPasswordRequest) {
        resetPasswordRequestsRepository.delete(resetPasswordRequest);
    }

    @Override
    public void deleteAll() {
        resetPasswordRequestsRepository.deleteAll();
    }

    @Override
    public Optional<ResetPasswordRequest> findById(Long id) {
        return resetPasswordRequestsRepository.findById(id);
    }

    public GenericResponse resetPassword(ResetPasswordRequestDto resetPassword) {
        ResetPasswordRequestDto currentDto = ResetPasswordRequestDto.ResetPasswordRequestToResetPasswordRequestDto(getAll().stream()
                .filter(currentResetPassword -> currentResetPassword.getConfirmationCode().equals(resetPassword.getConfirmationCode()))
                .findFirst()
                .get());
        if ((resetPassword.getUser().getTaxid().equalsIgnoreCase(currentDto.getUser().getTaxid())
                || resetPassword.getUser().getUsername().equalsIgnoreCase(currentDto.getUser().getUsername()))
                && currentDto.getExpirationDate().isAfter(LocalDateTime.now())
                && currentDto.isActive()) {
            userService.resetUser(currentDto.getUser());
            currentDto.setActive(false);
            create(ResetPasswordRequestDto.ResetPasswordRequestDtoToResetPasswordRequest(currentDto));
            return new GenericResponse("Se reinició la contraseña correctamente", true, "Ok");
        } else {
            throw new RuntimeException("Código Invalido o expirado");
        }

    }

    public GenericResponse add(String taxId) {
        UserDto currentUser = UserDto.UserToUserDto(userService.getAll().stream()
                .filter(user -> taxId.equals(user.getTaxid()))
                .findFirst()
                .get());
        ResetPasswordRequestDto request = new ResetPasswordRequestDto();
        request.setActive(true);
        request.setConfirmationCode(generateRecoveryCode());
        request.setExpirationDate(LocalDateTime.now().plusMinutes(10));
        request.setDeleted(false);
        request.setCreatedBy(currentUser.getUsername());
        request.setCreationDate(LocalDateTime.now());
        request.setLastUpdatedDate(LocalDateTime.now());
        request.setLastUpdatedBy(currentUser.getUsername());
        //request.setUser(UserDto.UserToUserDto(userRepository.findByTaxid(taxId).get()));
        request.setUser(currentUser);
        Optional<UserStatus> disableUserStatusOptional = userStatusService.findByDescription("INACTIVO");
        UserStatus disableUserStatus = disableUserStatusOptional.get();
        if (request.getUser().getStatus().getId() != disableUserStatus.getId()) {
            ResetPasswordRequest rprNew = ResetPasswordRequestDto.ResetPasswordRequestDtoToResetPasswordRequest(request);
            create(rprNew);
            sendRecoveryCode(request);
            return GenericResponse.builder()
                    .token(null)
                    .data(rprNew)
                    .build();

        } else {
            throw new GenericUserException("USUARIO INACTIVO");
        }
    }

    public String sendRecoveryCode(ResetPasswordRequestDto rpr) {
        String recoveryCode = rpr.getConfirmationCode();
        UserDto user = rpr.getUser();
        emailService.sendSimpleMessage(
                user.getEmail(),
                "Recuperación de contraseña para usuario:" + user.getUsername(),
                "Se ha solicitado un reinicio del password para el usuario: " + user.getUsername()
                + "\n\nTu código de recuperación es: " + recoveryCode
        );
        return "Correo enviado con éxito";
    }

    private String generateRecoveryCode() {
        int code = (int) (Math.random() * 900000) + 100000;
        return String.valueOf(code);
    }

    public List<ResetPasswordRequest> getAllActive() {
        return resetPasswordRequestsRepository.findAllByDeletedFalse();
    }

    public class GenericUserException extends RuntimeException {

        public GenericUserException(String message) {
            super(message);
        }
    }
}
