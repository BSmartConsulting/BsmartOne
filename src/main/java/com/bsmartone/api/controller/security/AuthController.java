package com.bsmartone.api.controller.security;

import com.bsmartone.api.dto.GenericResponse;
import com.bsmartone.api.dto.LoginRequest;
import com.bsmartone.api.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping(value = "login")
    public ResponseEntity<GenericResponse> login(@RequestBody LoginRequest request) {
        GenericResponse resp = authService.login(request);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + resp.getToken());
        resp.setToken(null);
        return new ResponseEntity<>(resp, headers, HttpStatus.OK);
    }
}
