/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bsmartone.api.controller.masterdata;

import com.bsmartone.api.dto.GenericResponse;
import com.bsmartone.api.dto.masterData.RoleDto;
import com.bsmartone.api.model.masterData.Role;
import com.bsmartone.api.model.masterData.User;
import com.bsmartone.api.service.JwtService;
import com.bsmartone.api.service.UserDetailsServiceImpl;
import com.bsmartone.api.service.masterData.RoleService;
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
@RequestMapping("api/v1/")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @GetMapping("/masterData/role")
    public GenericResponse list() {
        try {
            System.out.println("Inicia carga de Role:" + LocalDateTime.now());
            // Obtén solo las Role activas (no eliminadas)
            List<Role> lRole = roleService.getAllActive();
            // Usa Streams para transformar las entidades Role en DTOs
            List<RoleDto> lRoleDto = lRole.stream()
                    .map(RoleDto::RoleToRoleDto)
                    .collect(Collectors.toList());
            System.out.println("Finaliza carga de Role:" + LocalDateTime.now() + " con: " + lRoleDto.size() + " registros");
            return serviceResponse(lRoleDto, "OK");
        } catch (Exception e) {
            GlobalExceptionHandler.responseErrorMessage(e);
            return serviceResponse(null, e.getMessage());
        }
    }

    @GetMapping("/masterData/role/{id}")
    public GenericResponse get(@PathVariable long id) {
        try {
            System.out.println("Inicia carga de Role:" + LocalDateTime.now());
            // Obtén solo las Role activas (no eliminadas)
            List<Role> lRole = roleService.getAllActive();
            // Usa Streams para transformar las entidades Role en DTOs
            RoleDto lRoleDto = RoleDto.RoleToRoleDto(lRole.stream()
                    .filter(current -> current.getId() == id)
                    .findFirst()
                    .orElse(null));
            System.out.println("Finaliza busqueda  de Role:" + LocalDateTime.now() + " con: id: " + id);
            return serviceResponse(lRoleDto, "OK");
        } catch (Exception e) {
            GlobalExceptionHandler.responseErrorMessage(e);
            return serviceResponse(null, e.getMessage());
        }
    }

    @PostMapping("/masterData/role")
    public ResponseEntity<GenericResponse> post(@RequestBody RoleDto roledto) {
        try {
            if (roledto.getId() == null) {
                List<Role> roleList = roleService.getAll();
                for (Role current : roleList) {
                    if (current.getCode().equalsIgnoreCase(roledto.getCode())) {
                        roledto.setId(current.getId());
                        roledto.setDeleted(false);
                        break;
                    }
                }
            }

            // Convertir RoleDto a Role y crear el registro
            Role tmp = RoleDto.RoleDtoToRole(roledto);
            RoleDto result = RoleDto.RoleToRoleDto(roleService.create(tmp));
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

    @DeleteMapping("/masterData/role/{id}")
    public ResponseEntity<GenericResponse> delete(@PathVariable long id) {
        try {

            Role current = roleService.findById(id).get();
            current.setDeleted(false);
            RoleDto result = RoleDto.RoleToRoleDto(roleService.create(current));

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

    @DeleteMapping("/masterData/role")
    public ResponseEntity<GenericResponse> deleteAll() {
        try {
            List<Role> allRole = roleService.getAll();
            for (Role current : allRole) {
                current.setDeleted(false);
                roleService.create(current);
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
