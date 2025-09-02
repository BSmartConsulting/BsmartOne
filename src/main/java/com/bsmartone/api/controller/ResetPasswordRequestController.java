/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bsmartone.api.controller;

import com.bsmartone.api.dto.GenericResponse;
import com.bsmartone.api.dto.ResetPasswordRequestDto;
import com.bsmartone.api.model.ResetPasswordRequest;
import com.bsmartone.api.model.masterData.User;
import com.bsmartone.api.service.JwtService;
import com.bsmartone.api.service.ResetPasswordRequestService;
import com.bsmartone.api.service.UserDetailsServiceImpl;
import com.bsmartone.api.service.masterData.UserService;
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
public class ResetPasswordRequestController {

    @Autowired
    private ResetPasswordRequestService resetPasswordRequestService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @GetMapping("/transactional/resetPasswordRequest")
    public GenericResponse list() {
        try {
            System.out.println("Inicia carga de ResetPasswordRequest:" + LocalDateTime.now());
            // Obtén solo las ResetPasswordRequest activas (no eliminadas)
            List<ResetPasswordRequest> lResetPasswordRequest = resetPasswordRequestService.getAllActive();
            // Usa Streams para transformar las entidades ResetPasswordRequest en DTOs
            List<ResetPasswordRequestDto> lResetPasswordRequestDto = lResetPasswordRequest.stream()
                    .map(ResetPasswordRequestDto::ResetPasswordRequestToResetPasswordRequestDto)
                    .collect(Collectors.toList());
            System.out.println("Finaliza carga de ResetPasswordRequest:" + LocalDateTime.now() + " con: " + lResetPasswordRequestDto.size() + " registros");
            return serviceResponse(lResetPasswordRequestDto, "OK");
        } catch (Exception e) {
            GlobalExceptionHandler.responseErrorMessage(e);
            return serviceResponse(null, e.getMessage());
        }
    }

    @GetMapping("/transactional/resetPasswordRequest/{id}")
    public GenericResponse get(@PathVariable long id) {
        try {
            System.out.println("Inicia carga de ResetPasswordRequest:" + LocalDateTime.now());
            // Obtén solo las ResetPasswordRequest activas (no eliminadas)
            List<ResetPasswordRequest> lResetPasswordRequest = resetPasswordRequestService.getAllActive();
            // Usa Streams para transformar las entidades ResetPasswordRequest en DTOs
            ResetPasswordRequestDto lResetPasswordRequestDto = ResetPasswordRequestDto.ResetPasswordRequestToResetPasswordRequestDto(lResetPasswordRequest.stream()
                    .filter(current -> current.getId() == id)
                    .findFirst()
                    .orElse(null));
            System.out.println("Finaliza busqueda  de ResetPasswordRequest:" + LocalDateTime.now() + " con: id: " + id);
            return serviceResponse(lResetPasswordRequestDto, "OK");
        } catch (Exception e) {
            GlobalExceptionHandler.responseErrorMessage(e);
            return serviceResponse(null, e.getMessage());
        }
    }

    @PostMapping("/transactional/resetPasswordRequest/reset")
    public ResponseEntity<GenericResponse> post(@RequestBody ResetPasswordRequestDto resetPasswordRequestdto) {
        try {
            ResetPasswordRequestDto currentdto = new ResetPasswordRequestDto();
            if (resetPasswordRequestdto.getId() == null) {
                List<ResetPasswordRequest> resetPasswordRequestList = resetPasswordRequestService.getAll();
                for (ResetPasswordRequest current : resetPasswordRequestList) {
                    if (current.getConfirmationCode().equalsIgnoreCase(resetPasswordRequestdto.getConfirmationCode())) {
                        currentdto = ResetPasswordRequestDto.ResetPasswordRequestToResetPasswordRequestDto(current);
                        break;
                    }
                }
            }

            // Convertir ResetPasswordRequestDto a ResetPasswordRequest y crear el registro
            //ResetPasswordRequest tmp = ResetPasswordRequestDto.ResetPasswordRequestDtoToResetPasswordRequest(currentdto);
            //ResetPasswordRequestDto result = ResetPasswordRequestDto.ResetPasswordRequestToResetPasswordRequestDto(tmp);
            GenericResponse response = resetPasswordRequestService.resetPassword(currentdto); // Devolver la respuesta
            if (currentdto != null) {
                return new ResponseEntity<>(response, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            GlobalExceptionHandler.responseErrorMessage(e);
            return new ResponseEntity<>(new GenericResponse(null, null, e.toString()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/transactional/resetPasswordRequest/{resetPasswordRequest}")
    public ResponseEntity<GenericResponse> post(@PathVariable String resetPasswordRequest) {
        return ResponseEntity.ok(resetPasswordRequestService.add(resetPasswordRequest));
    }

    @DeleteMapping("/transactional/resetPasswordRequest/{id}")
    public ResponseEntity<GenericResponse> delete(@PathVariable long id) {
        try {

            ResetPasswordRequest current = resetPasswordRequestService.findById(id).get();
            current.setDeleted(false);
            ResetPasswordRequestDto result = ResetPasswordRequestDto.ResetPasswordRequestToResetPasswordRequestDto(resetPasswordRequestService.create(current));

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

    @DeleteMapping("/transactional/resetPasswordRequest")
    public ResponseEntity<GenericResponse> deleteAll() {
        try {
            List<ResetPasswordRequest> allResetPasswordRequest = resetPasswordRequestService.getAll();
            for (ResetPasswordRequest current : allResetPasswordRequest) {
                current.setDeleted(false);
                resetPasswordRequestService.create(current);
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
