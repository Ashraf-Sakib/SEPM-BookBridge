package com.bookbridge.BookBridge.service;

import com.bookbridge.BookBridge.dto.request.RegisterRequest;
import com.bookbridge.BookBridge.entity.Role;
import com.bookbridge.BookBridge.entity.User;
import com.bookbridge.BookBridge.exception.ResourceNotFoundException;
import com.bookbridge.BookBridge.repository.RoleRepository;
import com.bookbridge.BookBridge.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest request;

    @BeforeEach
    void setUp() {
        request = new RegisterRequest();
        request.setUsername("buyer1");
        request.setEmail("buyer1@example.com");
        request.setPassword("password123");
    }

    @Test
    void register_shouldSaveBuyerByDefault() {
        Role buyerRole = new Role(1, Role.RoleName.ROLE_BUYER);

        when(userRepository.existsByUsername("buyer1")).thenReturn(false);
        when(userRepository.existsByEmail("buyer1@example.com")).thenReturn(false);
        when(roleRepository.findByName(Role.RoleName.ROLE_BUYER)).thenReturn(Optional.of(buyerRole));
        when(passwordEncoder.encode("password123")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = authService.register(request);

        assertEquals("buyer1", result.getUsername());
        assertTrue(result.getRoles().stream().anyMatch(r -> r.getName() == Role.RoleName.ROLE_BUYER));
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_shouldSaveSellerWhenRequested() {
        request.setRole("SELLER");
        Role sellerRole = new Role(2, Role.RoleName.ROLE_SELLER);

        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(roleRepository.findByName(Role.RoleName.ROLE_SELLER)).thenReturn(Optional.of(sellerRole));
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = authService.register(request);

        assertTrue(result.getRoles().stream().anyMatch(r -> r.getName() == Role.RoleName.ROLE_SELLER));
    }

    @Test
    void register_shouldThrowWhenUsernameTaken() {
        when(userRepository.existsByUsername("buyer1")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> authService.register(request));

        assertEquals("Username already taken", ex.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_shouldThrowWhenRoleMissing() {
        request.setRole("ADMIN");

        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(roleRepository.findByName(Role.RoleName.ROLE_ADMIN)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> authService.register(request));
    }

    @Test
    void register_shouldThrowWhenRoleInvalid() {
        request.setRole("RANDOM");

        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> authService.register(request));
    }
}
