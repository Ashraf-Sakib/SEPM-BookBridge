package com.bookbridge.BookBridge.service;

import com.bookbridge.BookBridge.dto.response.UserResponse;
import com.bookbridge.BookBridge.entity.Role;
import com.bookbridge.BookBridge.entity.User;
import com.bookbridge.BookBridge.exception.ResourceNotFoundException;
import com.bookbridge.BookBridge.repository.RoleRepository;
import com.bookbridge.BookBridge.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public UserResponse getUserById(Integer userId) {
        return userRepository.findById(userId)
                .map(this::convertToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Transactional(readOnly = true)
    public UserResponse getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(this::convertToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Transactional
    public UserResponse updateUser(Integer userId, User userDetails) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        ensureNonAdminTarget(user, "delete");
        userRepository.deleteById(userId);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getUsersForAdmin(String roleFilter, String statusFilter) {
        return userRepository.findAll().stream()
                .sorted(Comparator.comparing(User::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .filter(user -> matchesRoleFilter(user, roleFilter))
                .filter(user -> matchesStatusFilter(user, statusFilter))
                .map(this::convertToResponse)
                .toList();
    }

    @Transactional
    public UserResponse assignMarketplaceRoles(Integer userId, Set<Role.RoleName> requestedRoles) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        ensureNonAdminTarget(user, "change roles for");

        Set<Role.RoleName> sanitizedRoles = EnumSet.noneOf(Role.RoleName.class);
        if (requestedRoles != null) {
            requestedRoles.stream()
                    .filter(role -> role == Role.RoleName.ROLE_BUYER || role == Role.RoleName.ROLE_SELLER)
                    .forEach(sanitizedRoles::add);
        }

        if (sanitizedRoles.isEmpty()) {
            throw new IllegalArgumentException("At least one marketplace role must be selected");
        }

        Set<Role> mappedRoles = sanitizedRoles.stream()
                .map(roleName -> roleRepository.findByName(roleName)
                        .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleName)))
                .collect(java.util.stream.Collectors.toSet());

        user.setRoles(mappedRoles);
        return convertToResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse disableUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        ensureNonAdminTarget(user, "disable");

        user.setEnabled(false);
        if (user.getDisabledAt() == null) {
            user.setDisabledAt(LocalDateTime.now());
        }

        return convertToResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse enableUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        ensureNonAdminTarget(user, "enable");

        user.setEnabled(true);
        user.setDisabledAt(null);
        user.setDeletionScheduledAt(null);

        return convertToResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse scheduleDeletion(Integer userId, int days) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        ensureNonAdminTarget(user, "schedule deletion for");

        user.setEnabled(false);
        if (user.getDisabledAt() == null) {
            user.setDisabledAt(LocalDateTime.now());
        }
        user.setDeletionScheduledAt(LocalDateTime.now().plusDays(days));

        return convertToResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse cancelScheduledDeletion(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        ensureNonAdminTarget(user, "cancel deletion for");

        user.setDeletionScheduledAt(null);
        return convertToResponse(userRepository.save(user));
    }

    @Transactional
    public int purgeUsersScheduledForDeletion(LocalDateTime now) {
        List<User> dueUsers = userRepository.findByDeletionScheduledAtBefore(now).stream()
                .filter(user -> !isAdmin(user))
                .toList();

        if (dueUsers.isEmpty()) {
            return 0;
        }

        userRepository.deleteAll(dueUsers);
        return dueUsers.size();
    }

    private UserResponse convertToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setEnabled(user.isEnabled());
        response.setDisabledAt(user.getDisabledAt());
        response.setDeletionScheduledAt(user.getDeletionScheduledAt());
        response.setRoles(user.getRoles().stream()
                .map(role -> role.getName().name())
                .sorted()
                .collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new)));
        return response;
    }

    private boolean matchesRoleFilter(User user, String roleFilter) {
        if (roleFilter == null || roleFilter.isBlank() || roleFilter.equalsIgnoreCase("ALL")) {
            return true;
        }

        Set<String> roleNames = user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(java.util.stream.Collectors.toSet());

        String normalized = roleFilter.trim().toUpperCase();
        return switch (normalized) {
            case "BUYER" -> roleNames.contains(Role.RoleName.ROLE_BUYER.name())
                    && !roleNames.contains(Role.RoleName.ROLE_SELLER.name());
            case "SELLER" -> roleNames.contains(Role.RoleName.ROLE_SELLER.name())
                    && !roleNames.contains(Role.RoleName.ROLE_BUYER.name());
            case "MIXED" -> roleNames.contains(Role.RoleName.ROLE_SELLER.name())
                    && roleNames.contains(Role.RoleName.ROLE_BUYER.name());
            case "ADMIN" -> roleNames.contains(Role.RoleName.ROLE_ADMIN.name());
            default -> true;
        };
    }

    private boolean matchesStatusFilter(User user, String statusFilter) {
        if (statusFilter == null || statusFilter.isBlank() || statusFilter.equalsIgnoreCase("ALL")) {
            return true;
        }

        String normalized = statusFilter.trim().toUpperCase();
        return switch (normalized) {
            case "ACTIVE" -> user.isEnabled() && user.getDeletionScheduledAt() == null;
            case "DISABLED" -> !user.isEnabled() && user.getDeletionScheduledAt() == null;
            case "PENDING_DELETE" -> user.getDeletionScheduledAt() != null;
            default -> true;
        };
    }

    private void ensureNonAdminTarget(User user, String action) {
        if (isAdmin(user)) {
            throw new IllegalArgumentException("Admin accounts are protected and cannot be " + action);
        }
    }

    private boolean isAdmin(User user) {
        return user.getRoles().stream().anyMatch(role -> role.getName() == Role.RoleName.ROLE_ADMIN);
    }
}
