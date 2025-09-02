/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bsmartone.api.controller.masterdata;

import com.bsmartone.api.dto.GenericResponse;
import com.bsmartone.api.dto.masterData.UserStatusDto;
import com.bsmartone.api.model.masterData.User;
import com.bsmartone.api.model.masterData.UserStatus;
import com.bsmartone.api.service.JwtService;
import com.bsmartone.api.service.UserDetailsServiceImpl;
import com.bsmartone.api.service.masterData.UserService;
import com.bsmartone.api.service.masterData.UserStatusService;
import com.bsmartone.api.util.GlobalExceptionHandler;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class UserStatusController {

    @Autowired
    private UserStatusService userStatusService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @GetMapping("/masterData/userStatus")
    public GenericResponse list() {
        try {
            System.out.println("Inicia carga de UserStatus:" + LocalDateTime.now());
            // Obtén solo las UserStatus activas (no eliminadas)
            List<UserStatus> lUserStatus = userStatusService.getAllActive();
            // Usa Streams para transformar las entidades UserStatus en DTOs
            List<UserStatusDto> lUserStatusDto = lUserStatus.stream()
                    .map(UserStatusDto::UserStatusToUserStatusDto)
                    .collect(Collectors.toList());
            System.out.println("Finaliza carga de UserStatus:" + LocalDateTime.now() + " con: " + lUserStatusDto.size() + " registros");
            return serviceResponse(lUserStatusDto, "OK");
        } catch (Exception e) {
            GlobalExceptionHandler.responseErrorMessage(e);
            return serviceResponse(null, e.getMessage());
        }
    }

    @GetMapping("/masterData/userStatus/{id}")
    public GenericResponse get(@PathVariable long id) {
        try {
            System.out.println("Inicia carga de UserStatus:" + LocalDateTime.now());
            // Obtén solo las UserStatus activas (no eliminadas)
            List<UserStatus> lUserStatus = userStatusService.getAllActive();
            // Usa Streams para transformar las entidades UserStatus en DTOs
            UserStatusDto lUserStatusDto = UserStatusDto.UserStatusToUserStatusDto(lUserStatus.stream()
                    .filter(current -> current.getId() == id)
                    .findFirst()
                    .orElse(null));
            System.out.println("Finaliza busqueda  de UserStatus:" + LocalDateTime.now() + " con: id: " + id);
            return serviceResponse(lUserStatusDto, "OK");
        } catch (Exception e) {
            GlobalExceptionHandler.responseErrorMessage(e);
            return serviceResponse(null, e.getMessage());
        }
    }

    @PostMapping("/masterData/userStatus")
    public ResponseEntity<GenericResponse> post(@RequestBody UserStatusDto userStatusdto) {
        try {
            if (userStatusdto.getId() == null) {
                List<UserStatus> userStatusList = userStatusService.getAll();
                for (UserStatus current : userStatusList) {
                    if (current.getCode().equalsIgnoreCase(userStatusdto.getCode())) {
                        userStatusdto.setId(current.getId());
                        userStatusdto.setDeleted(false);
                        break;
                    }
                }
            }

            // Convertir UserStatusDto a UserStatus y crear el registro
            UserStatus tmp = UserStatusDto.UserStatusDtoToUserStatus(userStatusdto);
            UserStatusDto result = UserStatusDto.UserStatusToUserStatusDto(userStatusService.create(tmp));
            // Devolver la respuesta
            if (result != null) {

                return new ResponseEntity<>(serviceResponse(result, "OK"), HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(serviceResponse(null, "ERROR"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            GlobalExceptionHandler.responseErrorMessage(e);
            return new ResponseEntity<>(serviceResponse(null, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/masterData/userStatus/{id}")
    public ResponseEntity<GenericResponse> delete(@PathVariable long id) {
        try {

            UserStatus current = userStatusService.findById(id).get();
            current.setDeleted(false);
            UserStatusDto result = UserStatusDto.UserStatusToUserStatusDto(userStatusService.create(current));

            // Devolver la respuesta
            if (result != null) {
                return new ResponseEntity<>(serviceResponse(result, "OK"), HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(serviceResponse(null, "ERROR"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            GlobalExceptionHandler.responseErrorMessage(e);
            return new ResponseEntity<>(serviceResponse(null, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/masterData/userStatus")
    public ResponseEntity<GenericResponse> deleteAll() {
        try {
            List<UserStatus> allUserStatus = userStatusService.getAll();
            for (UserStatus current : allUserStatus) {
                current.setDeleted(false);
                userStatusService.create(current);
            }
            return new ResponseEntity<>(serviceResponse(null, "OK"), HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            GlobalExceptionHandler.responseErrorMessage(e);
            return new ResponseEntity<>(serviceResponse(null, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    private <T> GenericResponse<T> serviceResponse(T data, String response) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();

        // Cargar el usuario desde la base de datos
        User authenticatedUser = userService.loadUserByUsername(username);
        String token = jwtService.getToken(userDetailsServiceImpl.loadUserByUsername(authenticatedUser.getUsername()), authenticatedUser);
        return GenericResponse.<T>builder()
                .token(token)
                .data(data)
                .response(response)
                .build();
    }
}
