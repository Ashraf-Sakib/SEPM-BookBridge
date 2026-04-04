package com.bookbridge.BookBridge.service;

import com.bookbridge.BookBridge.dto.response.UserResponse;
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

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setUsername("buyer1");
        user.setEmail("buyer1@example.com");
        user.setEnabled(true);
    }

    @Test
    void getUserById_shouldReturnUserResponse() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        UserResponse response = userService.getUserById(1);

        assertEquals("buyer1", response.getUsername());
        assertEquals("buyer1@example.com", response.getEmail());
    }

    @Test
    void getUserById_shouldThrowWhenNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(1));
    }

    @Test
    void getUserByUsername_shouldReturnUserResponse() {
        when(userRepository.findByUsername("buyer1")).thenReturn(Optional.of(user));

        UserResponse response = userService.getUserByUsername("buyer1");

        assertEquals(1, response.getId());
    }

    @Test
    void updateUser_shouldUpdateFieldsAndSave() {
        User update = new User();
        update.setUsername("buyer-updated");
        update.setEmail("updated@example.com");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = userService.updateUser(1, update);

        assertEquals("buyer-updated", response.getUsername());
        assertEquals("updated@example.com", response.getEmail());
    }

    @Test
    void deleteUser_shouldDeleteWhenExists() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        userService.deleteUser(1);

        verify(userRepository).deleteById(1);
    }

    @Test
    void assignMarketplaceRoles_shouldSetBuyerAndSellerRoles() {
        Role buyerRole = new Role(1, Role.RoleName.ROLE_BUYER);
        Role sellerRole = new Role(2, Role.RoleName.ROLE_SELLER);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(roleRepository.findByName(Role.RoleName.ROLE_BUYER)).thenReturn(Optional.of(buyerRole));
        when(roleRepository.findByName(Role.RoleName.ROLE_SELLER)).thenReturn(Optional.of(sellerRole));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = userService.assignMarketplaceRoles(1,
                Set.of(Role.RoleName.ROLE_BUYER, Role.RoleName.ROLE_SELLER));

        assertTrue(response.getRoles().contains("ROLE_BUYER"));
        assertTrue(response.getRoles().contains("ROLE_SELLER"));
    }

    @Test
    void scheduleDeletion_shouldDisableAndSetFutureDeletionDate() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = userService.scheduleDeletion(1, 14);

        assertFalse(response.isEnabled());
        assertNotNull(response.getDisabledAt());
        assertNotNull(response.getDeletionScheduledAt());
        assertTrue(response.getDeletionScheduledAt().isAfter(LocalDateTime.now().plusDays(13)));
    }
}
