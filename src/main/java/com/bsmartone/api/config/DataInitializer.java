package com.bsmartone.api.config;

import com.bsmartone.api.model.masterData.Role;
import com.bsmartone.api.model.masterData.User;
import com.bsmartone.api.model.masterData.UserRole;
import com.bsmartone.api.model.masterData.UserStatus;
import com.bsmartone.api.repository.masterData.RoleRepository;
import com.bsmartone.api.repository.masterData.UserRepository;
import com.bsmartone.api.repository.masterData.UserRoleRepository;
import com.bsmartone.api.repository.masterData.UserStatusRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner init(RoleRepository roleRepo,
                           UserRepository userRepo,
                           UserRoleRepository userRoleRepo,
                           UserStatusRepository statusRepo,
                           PasswordEncoder enc) {
        return args -> initData(roleRepo, userRepo, userRoleRepo, statusRepo, enc);
    }

    @Transactional
    void initData(RoleRepository roleRepo,
                  UserRepository userRepo,
                  UserRoleRepository userRoleRepo,
                  UserStatusRepository statusRepo,
                  PasswordEncoder enc) {

        // 1) Rol ADMIN
        Role adminRole = roleRepo.findByCode("ADMIN").orElseGet(() -> {
            Role r = new Role();
            r.setCode("ADMIN");
            r.setDescription("Administrador");
            return roleRepo.save(r);
        });

        // 2) Status ACTIVO (si User.status es NOT NULL)
        UserStatus active = statusRepo.findByCode("ACTIVE").orElseGet(() -> {
            UserStatus s = new UserStatus();
            s.setCode("ACTIVE");
            s.setDescription("Activo");
            return statusRepo.save(s);
        });

        // 3) Usuario admin (password = admin)
        User admin = userRepo.findByUsername("admin").orElseGet(() -> {
            User u = User.builder()
                    .username("admin")
                    .password(enc.encode("admin"))
                    .email("admin@local")
                    .taxid("999999999")
                    .fullname("Administrador")
                    .status(active)                 // @ManyToOne status_id
                    .requirespasswordchange(false)
                    .build();
            return userRepo.save(u);
        });
        try
        {
        // 4) Vincular admin ↔ ADMIN si no existe en la tabla puente
            UserRole link = new UserRole();
            link.setUser(admin);
            link.setRole(adminRole);
            userRoleRepo.save(link);
            
        }
        catch(Exception e)
        {
            System.out.println("El role ya existe");
        }
    }
}
