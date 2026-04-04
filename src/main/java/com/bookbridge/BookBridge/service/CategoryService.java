package com.bookbridge.BookBridge.service;

import com.bookbridge.BookBridge.dto.response.CategoryResponse;
import com.bookbridge.BookBridge.entity.Category;
import com.bookbridge.BookBridge.repository.CategoryRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(category -> new CategoryResponse(
                        category.getId(),
                        category.getName(),
                        category.getSlug(),
                        category.getDescription()))
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<Category> findBySlug(String slug) {
        if (slug == null || slug.isBlank()) {
            return Optional.empty();
        }
        String normalized = slug.trim();
        return categoryRepository.findAll().stream()
                .filter(category -> category.getSlug() != null)
                .filter(category -> category.getSlug().equalsIgnoreCase(normalized))
                .findFirst();
    }
}

