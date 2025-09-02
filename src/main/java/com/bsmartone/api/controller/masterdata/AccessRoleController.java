/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bsmartone.api.controller.masterdata;

import com.bsmartone.api.dto.GenericResponse;
import com.bsmartone.api.dto.masterData.AccessRoleDto;
import com.bsmartone.api.model.masterData.AccessRole;
import com.bsmartone.api.model.masterData.User;
import com.bsmartone.api.service.JwtService;
import com.bsmartone.api.service.UserDetailsServiceImpl;
import com.bsmartone.api.service.masterData.AccessRoleService;
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
public class AccessRoleController {

    @Autowired
    private AccessRoleService accessRoleService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @GetMapping("/masterData/accessRole")
    public GenericResponse list() {
        try {
            System.out.println("Inicia carga de AccessRole:" + LocalDateTime.now());
            // Obtén solo las AccessRole activas (no eliminadas)
            List<AccessRole> lAccessRole = accessRoleService.getAllActive();
            // Usa Streams para transformar las entidades AccessRole en DTOs
            List<AccessRoleDto> lAccessRoleDto = lAccessRole.stream()
                    .map(AccessRoleDto::AccessRoleToAccessRoleDto)
                    .collect(Collectors.toList());
            System.out.println("Finaliza carga de AccessRole:" + LocalDateTime.now() + " con: " + lAccessRoleDto.size() + " registros");
            return serviceResponse(lAccessRoleDto, "OK");
        } catch (Exception e) {
            GlobalExceptionHandler.responseErrorMessage(e);
            return serviceResponse(null, e.getMessage());
        }
    }

    @GetMapping("/masterData/accessRole/{id}")
    public GenericResponse get(@PathVariable long id) {
        try {
            System.out.println("Inicia carga de AccessRole:" + LocalDateTime.now());
            // Obtén solo las AccessRole activas (no eliminadas)
            List<AccessRole> lAccessRole = accessRoleService.getAllActive();
            // Usa Streams para transformar las entidades AccessRole en DTOs
            AccessRoleDto lAccessRoleDto = AccessRoleDto.AccessRoleToAccessRoleDto(lAccessRole.stream()
                    .filter(current -> current.getId() == id)
                    .findFirst()
                    .orElse(null));
            System.out.println("Finaliza busqueda  de AccessRole:" + LocalDateTime.now() + " con: id: " + id);
            return serviceResponse(lAccessRoleDto, "OK");
        } catch (Exception e) {
            GlobalExceptionHandler.responseErrorMessage(e);
            return serviceResponse(null, e.getMessage());
        }
    }

    @PostMapping("/masterData/accessRole")
    public ResponseEntity<GenericResponse> post(@RequestBody AccessRoleDto accessRoledto) {
        try {
            if (accessRoledto.getId() == null) {
                List<AccessRole> accessRoleList = accessRoleService.getAll();
                for (AccessRole current : accessRoleList) {
                    if (current.getRole().equals(accessRoledto.getRole().getId())
                            && current.getAccessdefinition().equals(accessRoledto.getAccessdefinition().getId())) {
                        accessRoledto.setId(current.getId());
                        accessRoledto.setDeleted(false);
                        break;
                    }
                }
            }

            // Convertir AccessRoleDto a AccessRole y crear el registro
            AccessRole tmp = AccessRoleDto.AccessRoleDtoToAccessRole(accessRoledto);
            AccessRoleDto result = AccessRoleDto.AccessRoleToAccessRoleDto(accessRoleService.create(tmp));
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

    @DeleteMapping("/masterData/accessRole/{id}")
    public ResponseEntity<GenericResponse> delete(@PathVariable long id) {
        try {

            AccessRole current = accessRoleService.findById(id);
            current.setDeleted(false);
            AccessRoleDto result = AccessRoleDto.AccessRoleToAccessRoleDto(accessRoleService.create(current));

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

    @DeleteMapping("/masterData/accessRole")
    public ResponseEntity<GenericResponse> deleteAll() {
        try {
            List<AccessRole> allAccessRole = accessRoleService.getAll();
            for (AccessRole current : allAccessRole) {
                current.setDeleted(false);
                accessRoleService.create(current);
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
