package com.bookbridge.BookBridge.repository;

import java.util.Optional;

import com.bookbridge.BookBridge.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Optional<Category> findBySlugIgnoreCase(String slug);
    boolean existsByNameIgnoreCase(String name);
}
