/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bsmartone.api.service;

import com.bsmartone.api.model.masterData.Role;
import com.bsmartone.api.model.masterData.User;
import com.bsmartone.api.model.masterData.UserRole;
import com.bsmartone.api.repository.masterData.UserRepository;
import com.bsmartone.api.repository.masterData.UserRoleRepository;
import com.bsmartone.api.service.masterData.RoleService;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private RoleService roleService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        Set<UserRole> userRoles = userRoleRepository.findByUserId(user.getId());
        Set<Role> rolesId = new HashSet<>();
        for (UserRole currentUserRole : userRoles) {
            rolesId.add(currentUserRole.getRole());
        }
        user.setRoles(rolesId);
        Collection<? extends GrantedAuthority> authorities = getAuthorities(user);

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), authorities);
    }

    public Collection<? extends GrantedAuthority> getAuthorities(User user) {
        // Obtain roles using the IDs stored in user
        List<Role> roles = roleService.getRolesByIds(user.getRoles());

        // Map roles to GrantedAuthority
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getCode()))
                .collect(Collectors.toList());
    }
}
