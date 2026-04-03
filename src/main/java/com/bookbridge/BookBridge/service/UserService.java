package com.bookbridge.BookBridge.service;

import com.bookbridge.BookBridge.dto.response.UserResponse;
import com.bookbridge.BookBridge.entity.User;
import com.bookbridge.BookBridge.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserResponse getUserById(Integer userId) {
        return userRepository.findById(userId)
                .map(this::convertToResponse)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional(readOnly = true)
    public UserResponse getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(this::convertToResponse)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public UserResponse updateUser(Integer userId, User userDetails) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (userDetails.getEmail() != null) {
            user.setEmail(userDetails.getEmail());
        }
        if (userDetails.getUsername() != null) {
            user.setUsername(userDetails.getUsername());
        }

        User updatedUser = userRepository.save(user);
        return convertToResponse(updatedUser);
    }

    @Transactional
    public void deleteUser(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(userId);
    }

    private UserResponse convertToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setEnabled(user.isEnabled());
        return response;
    }
}