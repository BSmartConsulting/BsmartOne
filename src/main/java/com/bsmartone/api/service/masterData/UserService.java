package com.bsmartone.api.service.masterData;

import com.bsmartone.api.dto.GenericResponse;
import com.bsmartone.api.dto.masterData.RoleDto;
import com.bsmartone.api.dto.masterData.UserDto;
import com.bsmartone.api.dto.masterData.UserStatusDto;
import com.bsmartone.api.model.masterData.Role;
import com.bsmartone.api.model.masterData.User;
import com.bsmartone.api.model.masterData.UserRole;
import com.bsmartone.api.model.masterData.UserStatus;
import com.bsmartone.api.repository.masterData.UserRepository;
import com.bsmartone.api.service.EmailService;
import com.bsmartone.api.service.JwtService;
import com.bsmartone.api.service.UserDetailsServiceImpl;
import com.bsmartone.api.util.GlobalExceptionHandler;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserServiceInterface {

    @Autowired
    private UserStatusService userStatusService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;
    @Autowired
    private UserRoleService userRoleService;

    @PersistenceContext
    private EntityManager entityManager;
    private static final int BATCH_SIZE = 250; // Ajustado a 500

    @PersistenceContext
    private EntityManager entityManagerRates;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleService roleService;

    @Autowired
    private EmailService emailService;


    private static final LocalDateTime END_OF_TIME = LocalDateTime.of(9999, 12, 31, 23, 59, 59);
    private static final LocalDateTime START_TIME = LocalDateTime.of(9999, 12, 31, 23, 59, 59);

    private List<User> modifiedUserTypeList = new ArrayList<>();

    @Override
    public User create(User user, String operacion) {
        User savedUser = userRepository.save(user);
        updateUserRoles(savedUser, user.getRoles());
        sendRecoveryCode(user, operacion);
        return savedUser;

    }

    private void updateUserRoles(User user, Set<Role> roleIds) {
        // Obtener los roles actuales del usuario desde la lista en memoria
        List<UserRole> currentRoles = userRoleService.getAll().stream()
                .filter(ur -> ur.getUser().getId().equals(user.getId()))
                .collect(Collectors.toList());

        // Crear un set con los IDs de los roles actuales
        Set<Role> currentRoleIds = currentRoles.stream()
                .map(UserRole::getRole)
                .collect(Collectors.toSet());

        // Determinar los roles a eliminar y a agregar
        Set<Role> rolesToRemove = currentRoleIds.stream()
                .filter(roleId -> !roleIds.contains(roleId))
                .collect(Collectors.toSet());

        Set<Role> rolesToAdd = roleIds.stream()
                .filter(roleId -> !currentRoleIds.contains(roleId))
                .collect(Collectors.toSet());

        // Eliminar los roles que ya no deben estar asignados
        if (!rolesToRemove.isEmpty()) {
            List<UserRole> userRolesToRemove = currentRoles.stream()
                    .filter(ur -> rolesToRemove.contains(ur.getRole()))
                    .collect(Collectors.toList());
            userRoleService.deleteAll(userRolesToRemove);
            // También eliminarlos de la lista en memoria
        }

        // Agregar los nuevos roles
        if (!rolesToAdd.isEmpty()) {
            List<UserRole> newUserRoles = rolesToAdd.stream()
                    .map(roleId -> {
                        UserRole userRole = new UserRole();
                        userRole.setRole(roleId);
                        userRole.setUser(user);
                        userRole.setDeleted(false);
                        userRole.setCreationDate(LocalDateTime.now());
                        userRole.setLastUpdatedDate(LocalDateTime.now());
                        userRole.setLastUpdatedBy(user.getLastUpdatedBy());
                        userRole.setCreatedBy(user.getLastUpdatedBy());
                        return userRole;
                    })
                    .collect(Collectors.toList());

            userRoleService.saveAll(newUserRoles);
        }
    }

    @Override
    public List<User> getAll() {
        List<User> lusers = userRepository.findAll();
        for (User currentUser : lusers) {
            //Set<UserRole> userRoles = userRoleService.findByUserId(currentUser.getId());
            List<UserRole> userRoles = userRoleService.getAll();
            Set<Role> rolesId = new HashSet<>();
            for (UserRole currentUserRole : userRoles) {
                if (currentUser.getId().equals(currentUserRole.getUser().getId())) {
                    rolesId.add(currentUserRole.getRole());
                }
            }
            currentUser.setRoles(rolesId);
        }
        return lusers;
    }

    @Override
    public void delete(User user) {
        userRepository.delete(user);
    }

    @Override
    public void deleteAll() {
        userRepository.deleteAll();
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public void resetUser(UserDto request) {
        User user = UserDto.UserDtoToUser(request);
        Optional<UserStatus> defaultUserStatusOptional = userStatusService.findByDescription("ACTIVO");
        if (!defaultUserStatusOptional.isPresent()) {
            throw new RuntimeException("El estado por defecto: \"ACTIVO\" no existe");
        }
        UserStatus defaultUserStatus = defaultUserStatusOptional.get();
        user.setPassword(passwordEncoder.encode("Password123456$"));
        user.setStatus(defaultUserStatus);
        user.setRequirespasswordchange(true);
        user.setFailedattempts(0);
        create(user, "RESET_USER");
    }

    public GenericResponse updatePassword(UserDto request) {

        request.setFailedattempts(0);
        UserStatus activeUserStatus = userStatusService.findByDescription("ACTIVO").get();
        request.setStatus(UserStatusDto.UserStatusToUserStatusDto(activeUserStatus));
        User currentUser = UserDto.UserDtoToUser(request);
        create(currentUser, "UPDATE_PASSWORD");
        String token = jwtService.getToken(userDetailsServiceImpl.loadUserByUsername(currentUser.getUsername()), currentUser);
        return GenericResponse.builder()
                .token(token)
                .data(UserDto.UserToUserDto(currentUser))
                .build();

    }

    public GenericResponse add(UserDto request) {
        if (request.getId() != null) {
            Optional<User> currentUser = findById(request.getId());
            Set<UserRole> userRoles = new HashSet<>();
            Set<Role> roles = new HashSet<>();
            for (RoleDto r : request.getRoles()) {
                roles.add(RoleDto.RoleDtoToRole(r));
            }

            User user = User.builder()
                    .id(request.getId())
                    .username(request.getUsername())
                    .password(currentUser.get().getPassword())
                    .status(UserStatusDto.UserStatusDtoToUserStatus(request.getStatus()))
                    .roles(roles)
                    .requirespasswordchange(request.isRequirespasswordchange())
                    .failedattempts(request.getFailedattempts())
                    .taxid(request.getTaxid())
                    .email(request.getEmail())
                    .fullname(request.getFullname())
                    .cityofresidence(request.getCityofresidence())
                    .address(request.getAddress())
                    .failedattempts(request.getFailedattempts())
                    .cellphone(request.getCellphone())
                    .startdate(request.getStartdate())
                    .enddate(request.getEnddate())
                    .lastUpdatedBy(request.getLastUpdatedBy())
                    .lastUpdatedDate(request.getLastUpdatedDate())
                    .createdBy(request.getCreatedBy())
                    .creationDate(request.getCreationDate())
                    .deleted(request.getDeleted())
                    .build();
            create(user, "UPDATE_USER");

            String token = jwtService.getToken(userDetailsServiceImpl.loadUserByUsername(request.getLastUpdatedBy()), user);
            return GenericResponse.builder()
                    .token(token)
                    .data(UserDto.UserToUserDto(user))
                    .build();

        } else {
            //
            //Optional<Role> defaultRoleOptional = roleService.findByDescription("USER"); // Asume que el ID del rol es 2
            Optional<Role> defaultRoleOptional = roleService.findByDescription("ADMIN"); // Asume que el ID del rol es 2
            Optional<UserStatus> defaultUserStatusOptional = userStatusService.findByDescription("ACTIVO");

            if (!defaultRoleOptional.isPresent()) {
                throw new RuntimeException("El rol por defecto: \"ADMIN\", no existe");
            }
            if (!defaultUserStatusOptional.isPresent()) {
                throw new RuntimeException("El estado por defecto: \"ACTIVO\" no existe");
            }
            Role defaultRole = defaultRoleOptional.get();
            UserStatus defaultUserStatus = defaultUserStatusOptional.get();
            // Asignar el rol al nuevo usuario
            Set<Role> roles = new HashSet<>();
            roles.add(defaultRole);

            User user = User.builder()
                    .id(request.getId())
                    .username(request.getUsername())
                    .password(passwordEncoder.encode("Password123456$"))
                    .status(defaultUserStatus)
                    .roles(roles)
                    .requirespasswordchange(true)
                    .failedattempts(request.getFailedattempts())
                    .taxid(request.getTaxid())
                    .email(request.getEmail())
                    .fullname(request.getFullname())
                    .cityofresidence(request.getCityofresidence())
                    .address(request.getAddress())
                    .failedattempts(request.getFailedattempts())
                    .startdate(request.getStartdate())
                    .enddate(request.getEnddate())
                    .cellphone(request.getCellphone())
                    .lastUpdatedBy(request.getLastUpdatedBy())
                    .lastUpdatedDate(request.getLastUpdatedDate())
                    .createdBy(request.getCreatedBy())
                    .creationDate(request.getCreationDate())
                    .deleted(request.getDeleted())
                    .build();
            create(user, "CREATE_USER");
            String token = jwtService.getToken(userDetailsServiceImpl.loadUserByUsername(request.getLastUpdatedBy()), user);
            return GenericResponse.builder()
                    .token(token)
                    .data(UserDto.UserToUserDto(user))
                    .build();
        }
    }

    @Transactional
    public GenericResponse saveAllUsers(List<UserDto> users, User authenticatedUser) {
        try {
            System.out.println("Inicia carga masiva de usuario a bd (api)" + LocalDateTime.now().toString());
            List<User> lusers = new ArrayList<>();
            List<User> lAllusers = getAllActive();
            
            modifiedUserTypeList.clear();
            
            for (UserDto cdto : users) {
                if (cdto.getStartdate().equals(LocalDateTime.MIN)) {
                    cdto.setStartdate(START_TIME);
                }
                if (cdto.getEnddate().equals(LocalDateTime.MAX)  || cdto.getEnddate().equals(LocalDateTime.MIN)) {
                    cdto.setEnddate(END_OF_TIME);
                }
                lusers.add(UserDto.UserDtoToUser(cdto));
                System.out.println("USER:" + cdto.toString());
            }

            // Crear un mapa para búsqueda rápida de usuarios existentes por taxid
            Map<String, User> existingUsersMap = new HashMap<>();
            for (User currentExist : lAllusers) {
                existingUsersMap.put(currentExist.getTaxid(), currentExist);
            }

            // Actualizar IDs en lusers
            for (User current : lusers) {
                User existingUser = existingUsersMap.get(current.getTaxid());
                if (existingUser != null) {
                    current.setId(existingUser.getId());
                    current.setCreatedBy(existingUser.getCreatedBy());
                    current.setCreationDate(existingUser.getCreationDate());
                    //almacenar lista de casos en que se cambie el tipo
                } else {
                    current.setCreatedBy(current.getLastUpdatedBy());
                    current.setCreationDate(current.getLastUpdatedDate());
                }
            }
            int batchCount = 0;
            System.out.println("Inicia carga masiva de usuario a bd proceso bacth" + LocalDateTime.now().toString());
            for (int i = 0; i < lusers.size(); i++) {
                entityManager.merge(lusers.get(i));
                batchCount++;

                if (batchCount % BATCH_SIZE == 0) {
                    // Flush and clear the entity manager to free memory.
                    System.out.println("Carga datos" + i);
                    entityManager.flush();
                    entityManager.clear();
                    batchCount = 0;
                }
            }

            if (batchCount > 0) {
                entityManager.flush();
                entityManager.clear();
            }

            Set<Role> defaultRole = new HashSet<>();
            for (Role r : roleService.getAllActive()) {
                if (r.getDescription().equalsIgnoreCase("ASESOR")) {
                    defaultRole.add(r);
                    break;
                }
            }
            // Usa el usuario autenticado
            for (User current : lusers) {
                updateUserRoles(current, defaultRole);
            }
            sendEmailsUserTypeChanges();
            System.out.println("termina carga masiva de usuario a bd (api)" + LocalDateTime.now().toString());
            String token = jwtService.getToken(userDetailsServiceImpl.loadUserByUsername(authenticatedUser.getUsername()), authenticatedUser);
            return GenericResponse.builder()
                    .token(token)
                    .data(users) // Ajusta esto según sea necesario
                    .build();
        } catch (Exception e) {
            GlobalExceptionHandler.responseErrorMessage(e);
            System.out.println("ERROR EN CARGA DE ARCHIVOS DE USUARIO:" + e.getMessage());
            String token = jwtService.getToken(userDetailsServiceImpl.loadUserByUsername(authenticatedUser.getUsername()), authenticatedUser);
            return GenericResponse.builder()
                    .token(token)
                    .data(users) // Ajusta esto según sea necesario
                    .build();

        }
    }

    public User loadUserByUsername(String username) {
        System.out.println("Inicia carga masiva de usuario(Service)" + LocalDateTime.now().toString());
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("El usuario : " + username + " No existe"));
    }

    public String sendRecoveryCode(User user, String Operacion) {
        //modificar el texto segun la op
        String message = "";
        String messageTitle = "";
        switch (Operacion) {
            case "CREATE_USER":
                messageTitle = "Creación exitosa del usuario:" + user.getUsername();
                message = "Se ha creado exitosamente el usuario: " + user.getUsername()
                        + "\nPara iniciar sesión debe ingresar con el password: Password123456$"
                        + "\nAl iniciar sesión se solicitará cambiar contraseña, tenga en cuenta las siguientes restricciones:."
                        + "\n1. Debe contener mas de 8 carácteres"
                        + "\n2. Debe contener al menos una letra mayuscula"
                        + "\n3. Debe contener al menos una letra minuscula"
                        + "\n4. Debe contener al menos un número"
                        + "\n5. Debe contener al menos un caracter especial de la siguiente lista: @ # $ % ^ & + = * + - _ , ; : < > { } [ ]";
                emailService.sendSimpleMessage(user.getEmail(), messageTitle, message);
                break;
            case " UPDATE_USER":
                messageTitle = "Modificación exitosa del usuario:" + user.getUsername();
                message = "Se ha modificado exitosamente el usuario: " + user.getUsername()
                        + "\nPara iniciar sesión debe ingresar con el password: Password123456$"
                        + "\nAl iniciar sesión se solicitará cambiar contraseña, tenga en cuenta las siguientes restricciones:."
                        + "\n1. Debe contener mas de 8 carácteres"
                        + "\n2. Debe contener al menos una letra mayuscula"
                        + "\n3. Debe contener al menos una letra minuscula"
                        + "\n4. Debe contener al menos un número"
                        + "\n5. Debe contener al menos un caracter especial de la siguiente lista: @ # $ % ^ & + = * + - _ , ; : < > { } [ ]";
                break;
            case "RESET_USER":
                messageTitle = "Reinicio de cuenta exitosa para el usuario:" + user.getUsername();
                message = "Se ha reiniciado exitosamente el usuario: " + user.getUsername()
                        + "\nPara iniciar sesión debe ingresar con el password: Password123456$"
                        + "\nAl iniciar sesión se solicitará cambiar contraseña, tenga en cuenta las siguientes restricciones:."
                        + "\n1. Debe contener mas de 8 carácteres"
                        + "\n2. Debe contener al menos una letra mayuscula"
                        + "\n3. Debe contener al menos una letra minuscula"
                        + "\n4. Debe contener al menos un número"
                        + "\n5. Debe contener al menos un caracter especial de la siguiente lista: @ # $ % ^ & + = * + - _ , ; : < > { } [ ]";
                emailService.sendSimpleMessage(user.getEmail(), messageTitle, message);
                break;
            case "UPDATE_PASSWORD":
                messageTitle = "Cambio de password exitoso del usuario:" + user.getUsername();
                message = "Se ha modificado exitosamente el password del usuario: " + user.getUsername();
                emailService.sendSimpleMessage(user.getEmail(), messageTitle, message);
                break;

        }
        return "Correo enviado con éxito";
    }

    public List<User> getAllActive() {
        return userRepository.findAllByDeletedFalse();
    }

    private void sendEmailsUserTypeChanges() {
        String messageTitle = "";
        String message = "";
        String email="direccionlogistica@bsmartonegestionriesgo.com";
        System.out.println("Email configurado: "+email);
        //String email = "jaimesarmientop@gmail.com";
        for (User currentUser : modifiedUserTypeList) {
            System.out.println("Email de cambio de tipo de usuario enviado para el usuario:" + currentUser.getFullname());
            System.out.println("Email de cambio de tipo de usuario enviado al correo:" + email);
            messageTitle = "Usuario con cambio de tipo:" + currentUser.getFullname();
            message = " Se ha realizado un cambio de tipo de usuario para : " + currentUser.getFullname()
                    + "\n Este cambio puede afectar procesos de liquidación y otros ";
            emailService.sendSimpleMessage(email, messageTitle, message);

        }

    }
}
