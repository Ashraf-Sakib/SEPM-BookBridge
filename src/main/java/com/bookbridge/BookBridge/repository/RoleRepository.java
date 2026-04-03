package com.bookbridge.BookBridge.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookbridge.BookBridge.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(Role.RoleName name);
}
