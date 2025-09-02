package com.bsmartone.api.service;

import com.bsmartone.api.dto.GenericResponse;
import com.bsmartone.api.dto.LoginRequest;
import com.bsmartone.api.dto.masterData.UserDto;
import com.bsmartone.api.model.masterData.User;
import com.bsmartone.api.model.masterData.UserStatus;
import com.bsmartone.api.service.masterData.RoleService;
import com.bsmartone.api.service.masterData.UserRoleService;
import com.bsmartone.api.service.masterData.UserService;
import com.bsmartone.api.service.masterData.UserStatusService;
import com.bsmartone.api.util.GlobalExceptionHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserStatusService userStatusService;
    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;
    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private RoleService roleService;


    @Autowired
    private UserStatusService UserStatusService;

    public GenericResponse login(LoginRequest request) {
        User user = null;
        UserDto userdto = null;
        for (User current : userService.getAllActive()) {
            if (current.getUsername().equalsIgnoreCase(request.getUsername())) {
                user = current;
                for (User currentDto : userService.getAllActive()) {
                    if (currentDto.getId() == user.getId()) {
                        userdto = UserDto.UserToUserDto(currentDto);
                        break;
                    }
                }
                break;
            }
        }
        if (userdto == null) {
            throw new UserNotFoundException("EL USUARIO NO EXISTE");
        } else {
            String statusCode = userdto.getStatus().getDescription();
            if ("BLOQUEADO".equalsIgnoreCase(statusCode) || "INACTIVO".equalsIgnoreCase(statusCode)) {
                throw new InactiveUserException("USUARIO " + statusCode);
            }

            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                handleFailedAttempt(UserDto.UserDtoToUser(userdto));
                throw new InvalidPasswordException("CONTRASEÑA INCORRECTA");
            }

            try {
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
                );
            } catch (AuthenticationException e) {
                GlobalExceptionHandler.responseErrorMessage(e);
                handleFailedAttempt(UserDto.UserDtoToUser(userdto));
                throw new InvalidPasswordException("ERROR: CONTRASEÑA INCORRECTA");
            }

            String token = jwtService.getToken(userDetailsServiceImpl.loadUserByUsername(user.getUsername()), user);

            resetFailedAttempts(user);
            UserDto currentDto = null;
            for (User current : userService.getAllActive()) {
                if (current.getId() == user.getId()) {
                    currentDto = UserDto.UserToUserDto(current);
                    break;
                }
            }
            return GenericResponse.builder()
                    .token(token)
                    .data(currentDto)
                    .build();
        }
    }

    private void handleFailedAttempt(User user) {
        user.setFailedattempts(user.getFailedattempts() + 1);
        if (user.getFailedattempts() >= 3) {
            UserStatus blockedStatus = userStatusService.findByDescription("BLOQUEADO").get();
            user.setStatus(blockedStatus);
        }
        userService.create(user, "");
    }

    private void resetFailedAttempts(User user) {
        user.setFailedattempts(0);
        userService.create(user, "");
    }

    public class UserNotFoundException extends RuntimeException {

        public UserNotFoundException(String message) {
            super(message);
        }
    }

    public class InvalidPasswordException extends RuntimeException {

        public InvalidPasswordException(String message) {
            super(message);
        }
    }

    public class InactiveUserException extends RuntimeException {

        public InactiveUserException(String message) {
            super(message);
        }
    }
}
