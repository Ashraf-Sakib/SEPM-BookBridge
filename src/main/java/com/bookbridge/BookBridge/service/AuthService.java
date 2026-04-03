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

import java.util.Locale;
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

        Role.RoleName roleName = mapRole(request.getRole());

        Role userRole = roleRepository
            .findByName(roleName)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Role not found: " + roleName));

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .enabled(true)
                .roles(Set.of(userRole))
                .build();

        return userRepository.save(user);
    }

    private Role.RoleName mapRole(String role) {
        if (role == null || role.isBlank()) {
            return Role.RoleName.ROLE_BUYER;
        }

        String normalized = role.trim().toUpperCase(Locale.ROOT);
        return switch (normalized) {
            case "BUYER", "ROLE_BUYER" -> Role.RoleName.ROLE_BUYER;
            case "SELLER", "ROLE_SELLER" -> Role.RoleName.ROLE_SELLER;
            case "ADMIN", "ROLE_ADMIN" -> Role.RoleName.ROLE_ADMIN;
            default -> throw new IllegalArgumentException("Unsupported role: " + role);
        };
    }
}