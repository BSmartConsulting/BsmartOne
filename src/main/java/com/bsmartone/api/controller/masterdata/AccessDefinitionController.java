/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bsmartone.api.controller.masterdata;

import com.bsmartone.api.dto.GenericResponse;
import com.bsmartone.api.dto.masterData.AccessDefinitionDto;
import com.bsmartone.api.model.masterData.AccessDefinition;
import com.bsmartone.api.model.masterData.User;
import com.bsmartone.api.service.JwtService;
import com.bsmartone.api.service.UserDetailsServiceImpl;
import com.bsmartone.api.service.masterData.AccessDefinitionService;
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
public class AccessDefinitionController {

    @Autowired
    private AccessDefinitionService accessDefinitionService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @GetMapping("/masterData/accessDefinition")
    public GenericResponse list() {
        try {
            System.out.println("Inicia carga de AccessDefinition:" + LocalDateTime.now());
            // Obtén solo las AccessDefinition activas (no eliminadas)
            List<AccessDefinition> lAccessDefinition = accessDefinitionService.getAllActive();
            // Usa Streams para transformar las entidades AccessDefinition en DTOs
            List<AccessDefinitionDto> lAccessDefinitionDto = lAccessDefinition.stream()
                    .map(AccessDefinitionDto::AccessDefinitionToAccessDefinitionDto)
                    .collect(Collectors.toList());
            System.out.println("Finaliza carga de AccessDefinition:" + LocalDateTime.now() + " con: " + lAccessDefinitionDto.size() + " registros");
            return serviceResponse(lAccessDefinitionDto, "OK");
        } catch (Exception e) {
            GlobalExceptionHandler.responseErrorMessage(e);
            return serviceResponse(null, e.getMessage());
        }
    }

    @GetMapping("/masterData/accessDefinition/{id}")
    public GenericResponse get(@PathVariable long id) {
        try {
            System.out.println("Inicia carga de AccessDefinition:" + LocalDateTime.now());
            // Obtén solo las AccessDefinition activas (no eliminadas)
            List<AccessDefinition> lAccessDefinition = accessDefinitionService.getAllActive();
            // Usa Streams para transformar las entidades AccessDefinition en DTOs
            AccessDefinitionDto lAccessDefinitionDto = AccessDefinitionDto.AccessDefinitionToAccessDefinitionDto(lAccessDefinition.stream()
                    .filter(current -> current.getId() == id)
                    .findFirst()
                    .orElse(null));
            System.out.println("Finaliza busqueda  de AccessDefinition:" + LocalDateTime.now() + " con: id: " + id);
            return serviceResponse(lAccessDefinitionDto, "OK");
        } catch (Exception e) {
            GlobalExceptionHandler.responseErrorMessage(e);
            return serviceResponse(null, e.getMessage());
        }
    }

    @PostMapping("/masterData/accessDefinition")
    public ResponseEntity<GenericResponse> post(@RequestBody AccessDefinitionDto accessDefinitiondto) {
        try {
            if (accessDefinitiondto.getId() == null) {
                List<AccessDefinition> accessDefinitionList = accessDefinitionService.getAll();
                for (AccessDefinition current : accessDefinitionList) {
                    if (current.getCode().equalsIgnoreCase(accessDefinitiondto.getCode())) {
                        accessDefinitiondto.setId(current.getId());
                        accessDefinitiondto.setDeleted(false);
                        break;
                    }
                }
            }

            // Convertir AccessDefinitionDto a AccessDefinition y crear el registro
            AccessDefinition tmp = AccessDefinitionDto.AccessDefinitionDtoToAccessDefinition(accessDefinitiondto);
            AccessDefinitionDto result = AccessDefinitionDto.AccessDefinitionToAccessDefinitionDto(accessDefinitionService.create(tmp));
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

    @DeleteMapping("/masterData/accessDefinition/{id}")
    public ResponseEntity<GenericResponse> delete(@PathVariable long id) {
        try {

            AccessDefinition current = accessDefinitionService.findById(id);
            current.setDeleted(false);
            AccessDefinitionDto result = AccessDefinitionDto.AccessDefinitionToAccessDefinitionDto(accessDefinitionService.create(current));

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

    @DeleteMapping("/masterData/accessDefinition")
    public ResponseEntity<GenericResponse> deleteAll() {
        try {
            List<AccessDefinition> allAccessDefinition = accessDefinitionService.getAll();
            for (AccessDefinition current : allAccessDefinition) {
                current.setDeleted(false);
                accessDefinitionService.create(current);
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
