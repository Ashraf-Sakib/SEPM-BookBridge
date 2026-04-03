package com.bookbridge.BookBridge.service;

import com.bookbridge.BookBridge.dto.response.UserResponse;
import com.bookbridge.BookBridge.entity.User;
import com.bookbridge.BookBridge.exception.ResourceNotFoundException;
import com.bookbridge.BookBridge.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

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
        when(userRepository.existsById(1)).thenReturn(true);

        userService.deleteUser(1);

        verify(userRepository).deleteById(1);
    }
}
