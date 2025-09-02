/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bsmartone.api.controller.masterdata;

import com.bsmartone.api.dto.GenericResponse;
import com.bsmartone.api.dto.masterData.CityDto;
import com.bsmartone.api.model.masterData.City;
import com.bsmartone.api.model.masterData.User;
import com.bsmartone.api.service.JwtService;
import com.bsmartone.api.service.UserDetailsServiceImpl;
import com.bsmartone.api.service.masterData.CityService;
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
public class CityController {

    @Autowired
    private CityService cityService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @GetMapping("/masterData/city")
    public GenericResponse list() {
        try {
            System.out.println("Inicia carga de City:" + LocalDateTime.now());
            // Obtén solo las City activas (no eliminadas)
            List<City> lCity = cityService.getAllActive();
            // Usa Streams para transformar las entidades City en DTOs
            List<CityDto> lCityDto = lCity.stream()
                    .map(CityDto::CityToCityDto)
                    .collect(Collectors.toList());
            System.out.println("Finaliza carga de City:" + LocalDateTime.now() + " con: " + lCityDto.size() + " registros");
            return serviceResponse(lCityDto, "OK");
        } catch (Exception e) {
            GlobalExceptionHandler.responseErrorMessage(e);
            return serviceResponse(null, e.getMessage());
        }
    }

    @GetMapping("/masterData/city/{id}")
    public GenericResponse get(@PathVariable long id) {
        try {
            System.out.println("Inicia carga de City:" + LocalDateTime.now());
            // Obtén solo las City activas (no eliminadas)
            List<City> lCity = cityService.getAllActive();
            // Usa Streams para transformar las entidades City en DTOs
            CityDto lCityDto = CityDto.CityToCityDto(lCity.stream()
                    .filter(current -> current.getId() == id)
                    .findFirst()
                    .orElse(null));
            System.out.println("Finaliza busqueda  de City:" + LocalDateTime.now() + " con: id: " + id);
            return serviceResponse(lCityDto, "OK");
        } catch (Exception e) {
            GlobalExceptionHandler.responseErrorMessage(e);
            return serviceResponse(null, e.getMessage());
        }
    }

    @PostMapping("/masterData/city")
    public ResponseEntity<GenericResponse> post(@RequestBody CityDto citydto) {
        try {
            if (citydto.getId() == null) {
                List<City> cityList = cityService.getAll();
                for (City current : cityList) {
                    if (current.getCode().equalsIgnoreCase(citydto.getCode())) {
                        citydto.setId(current.getId());
                        citydto.setDeleted(false);
                        break;
                    }
                }
            }

            // Convertir CityDto a City y crear el registro
            City tmp = CityDto.CityDtoToCity(citydto);
            CityDto result = CityDto.CityToCityDto(cityService.create(tmp));
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

    @DeleteMapping("/masterData/city/{id}")
    public ResponseEntity<GenericResponse> delete(@PathVariable long id) {
        try {

            City current = cityService.findById(id);
            current.setDeleted(false);
            CityDto result = CityDto.CityToCityDto(cityService.create(current));

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

    @DeleteMapping("/masterData/city")
    public ResponseEntity<GenericResponse> deleteAll() {
        try {
            List<City> allCity = cityService.getAll();
            for (City current : allCity) {
                current.setDeleted(false);
                cityService.create(current);
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
