package com.bookbridge.BookBridge.config;

import com.bookbridge.BookBridge.entity.Category;
import com.bookbridge.BookBridge.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (categoryRepository.count() == 0) {
            categoryRepository.save(new Category(null, "Fiction", "fiction", "Novels and short stories"));
            categoryRepository.save(new Category(null, "Non-Fiction", "non-fiction", "Educational and informative books"));
            categoryRepository.save(new Category(null, "Science & Technology", "science-technology", "Science, tech, and computing books"));
            categoryRepository.save(new Category(null, "History", "history", "Historical accounts and biographies"));
            categoryRepository.save(new Category(null, "Self-Help", "self-help", "Personal development and motivation"));
            categoryRepository.save(new Category(null, "Business", "business", "Business, economics, and entrepreneurship"));
            log.info("Seeded default categories: {}", categoryRepository.count());
        } else {
            log.info("Categories already present: {}", categoryRepository.count());
        }
    }
}


