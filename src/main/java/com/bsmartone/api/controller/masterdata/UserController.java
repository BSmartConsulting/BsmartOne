/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bsmartone.api.controller.masterdata;

import com.bsmartone.api.dto.GenericResponse;
import com.bsmartone.api.dto.masterData.UserDto;
import com.bsmartone.api.model.masterData.User;
import com.bsmartone.api.service.JwtService;
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

/**
 *
 * @author jaime
 */
@RestController
@RequestMapping("/api/v1")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @GetMapping("/masterData/user")
    public GenericResponse list() {
        try {
            System.out.println("Inicia carga de User:" + LocalDateTime.now());
            // Obtén solo las User activas (no eliminadas)
            List<User> lUser = userService.getAllActive();
            // Usa Streams para transformar las entidades User en DTOs
            List<UserDto> lUserDto = lUser.stream()
                    .map(UserDto::UserToUserDto)
                    .collect(Collectors.toList());
            System.out.println("Finaliza carga de User:" + LocalDateTime.now() + " con: " + lUserDto.size() + " registros");
            return serviceResponse(lUserDto, "OK");
        } catch (Exception e) {
            GlobalExceptionHandler.responseErrorMessage(e);
            return serviceResponse(null, e.getMessage());
        }
    }

    @GetMapping("/masterData/user/{id}")
    public GenericResponse get(@PathVariable long id) {
        try {
            System.out.println("Inicia carga de User:" + LocalDateTime.now());
            // Obtén solo las User activas (no eliminadas)
            List<User> lUser = userService.getAllActive();
            // Usa Streams para transformar las entidades User en DTOs
            UserDto lUserDto = UserDto.UserToUserDto(lUser.stream()
                    .filter(current -> current.getId() == id)
                    .findFirst()
                    .orElse(null));
            System.out.println("Finaliza busqueda  de User:" + LocalDateTime.now() + " con: id: " + id);
            return serviceResponse(lUserDto, "OK");
        } catch (Exception e) {
            GlobalExceptionHandler.responseErrorMessage(e);
            return serviceResponse(null, e.getMessage());
        }
    }

    @PostMapping("/masterData/user")
    public ResponseEntity<GenericResponse> post(@RequestBody UserDto userdto) {
        try {
            String operacion = "CREATE_USER";
            if (userdto.getId() == null) {
                List<User> userList = userService.getAll();
                for (User current : userList) {
                    if (current.getTaxid().equalsIgnoreCase(userdto.getTaxid())) {
                        userdto.setId(current.getId());
                        userdto.setDeleted(false);
                        operacion = "UPDATE_USER";
                        break;
                    }
                }
            }

            UserDto result = (UserDto) userService.add(userdto).getData();
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

    @DeleteMapping("/masterData/user/{id}")
    public ResponseEntity<GenericResponse> delete(@PathVariable long id) {
        try {

            User current = userService.findById(id).get();
            current.setDeleted(false);
            UserDto result = UserDto.UserToUserDto(userService.create(current, "DELETE_USER"));

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

    @DeleteMapping("/masterData/user")
    public ResponseEntity<GenericResponse> deleteAll() {
        try {
            List<User> allUser = userService.getAll();
            for (User current : allUser) {
                current.setDeleted(false);
                userService.create(current, "DELETE_USER");
            }
            return new ResponseEntity<>(serviceResponse(null, "OK"), HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            GlobalExceptionHandler.responseErrorMessage(e);
            return new ResponseEntity<>(serviceResponse(null, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping("/masterData/user/load")
    public ResponseEntity<GenericResponse> post(@RequestBody List<UserDto> users) {
        // Obtener el usuario autenticado desde el contexto de seguridad
        System.out.println("Inicia carga masiva de usuario" + LocalDateTime.now().toString());
        System.out.println("usuarios" + users.toString());
        try {

            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String username = userDetails.getUsername();

            // Cargar el usuario desde la base de datos
            User authenticatedUser = userService.loadUserByUsername(username);

            // Pasar el usuario autenticado al servicio
            List<UserDto> result = (List<UserDto>) userService.saveAllUsers(users, authenticatedUser).getData();
            return new ResponseEntity<>(serviceResponse(result, "OK"), HttpStatus.CREATED);

        } catch (Exception e) {
            GlobalExceptionHandler.responseErrorMessage(e);
            System.out.println("ERROR EN CARGA DE USUARIOS:" + e.getMessage());
            return new ResponseEntity<>(serviceResponse(null, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/masterData/user/updatePassword")
    public ResponseEntity<GenericResponse> updatePassword(@RequestBody UserDto user) {
        try {
            GenericResponse result = userService.updatePassword(user);
            return new ResponseEntity<>(serviceResponse(result, "OK"), HttpStatus.CREATED);
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
