package com.bookbridge.BookBridge.service;

import com.bookbridge.BookBridge.dto.request.RegisterRequest;
import com.bookbridge.BookBridge.entity.Role;
import com.bookbridge.BookBridge.entity.User;
import com.bookbridge.BookBridge.exception.ResourceNotFoundException;
import com.bookbridge.BookBridge.repository.RoleRepository;
import com.bookbridge.BookBridge.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public User register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername()))
            throw new IllegalArgumentException(
                    "Username already taken");

        if (userRepository.existsByEmail(request.getEmail()))
            throw new IllegalArgumentException(
                    "Email already registered");

        Role.RoleName requestedRole = request.getRole() == null
                ? Role.RoleName.ROLE_BUYER
                : request.getRole();

        if (requestedRole == Role.RoleName.ROLE_ADMIN) {
            throw new IllegalArgumentException(
                    "Admin accounts cannot be registered");
        }

        Role assignedRole = roleRepository
            .findByName(requestedRole)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Role not found: " + requestedRole));

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .enabled(true)
                .roles(Set.of(assignedRole))
                .build();

        return userRepository.save(user);
    }
}