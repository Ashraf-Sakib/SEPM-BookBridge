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
        request.setRole(Role.RoleName.ROLE_BUYER);
    }

    @Test
    void register_shouldSaveBuyerWithBuyerRole() {
        Role buyerRole = new Role(1, Role.RoleName.ROLE_BUYER);

        when(userRepository.existsByUsername("buyer1")).thenReturn(false);
        when(userRepository.existsByEmail("buyer1@example.com")).thenReturn(false);
        when(roleRepository.findByName(Role.RoleName.ROLE_BUYER)).thenReturn(Optional.of(buyerRole));
        when(passwordEncoder.encode("password123")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = authService.register(request);

        assertEquals("buyer1", result.getUsername());
        assertTrue(result.getRoles().stream().anyMatch(r -> r.getName() == Role.RoleName.ROLE_BUYER));
        assertEquals(1, result.getRoles().size());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_shouldSaveSellerWithSellerRole() {
        request.setRole(Role.RoleName.ROLE_SELLER);

        Role sellerRole = new Role(2, Role.RoleName.ROLE_SELLER);

        when(userRepository.existsByUsername("buyer1")).thenReturn(false);
        when(userRepository.existsByEmail("buyer1@example.com")).thenReturn(false);
        when(roleRepository.findByName(Role.RoleName.ROLE_SELLER)).thenReturn(Optional.of(sellerRole));
        when(passwordEncoder.encode("password123")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = authService.register(request);

        assertTrue(result.getRoles().stream().anyMatch(r -> r.getName() == Role.RoleName.ROLE_SELLER));
        assertEquals(1, result.getRoles().size());
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
    void register_shouldThrowWhenEmailTaken() {
        when(userRepository.existsByUsername("buyer1")).thenReturn(false);
        when(userRepository.existsByEmail("buyer1@example.com")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> authService.register(request));

        assertEquals("Email already registered", ex.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_shouldThrowWhenBuyerRoleMissing() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(roleRepository.findByName(Role.RoleName.ROLE_BUYER)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> authService.register(request));

        assertEquals("Role not found: ROLE_BUYER", ex.getMessage());
    }

    @Test
    void register_shouldThrowWhenSellerRoleMissing() {
        request.setRole(Role.RoleName.ROLE_SELLER);

        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(roleRepository.findByName(Role.RoleName.ROLE_SELLER)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> authService.register(request));

        assertEquals("Role not found: ROLE_SELLER", ex.getMessage());
    }

    @Test
    void register_shouldRejectAdminRole() {
        request.setRole(Role.RoleName.ROLE_ADMIN);

        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> authService.register(request));

        assertEquals("Admin accounts cannot be registered", ex.getMessage());
        verifyNoInteractions(roleRepository);
    }
}
