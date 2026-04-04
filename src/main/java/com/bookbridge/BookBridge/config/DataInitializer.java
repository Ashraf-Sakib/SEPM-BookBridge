package com.bookbridge.BookBridge.config;

import com.bookbridge.BookBridge.entity.Category;
import com.bookbridge.BookBridge.entity.Role;
import com.bookbridge.BookBridge.entity.User;
import com.bookbridge.BookBridge.repository.CategoryRepository;
import com.bookbridge.BookBridge.repository.RoleRepository;
import com.bookbridge.BookBridge.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void run(String... args) {
        migrateCategorySlugIfNeeded();

        if (roleRepository.count() == 0) {
            roleRepository.save(new Role(null, Role.RoleName.ROLE_ADMIN));
            roleRepository.save(new Role(null, Role.RoleName.ROLE_SELLER));
            roleRepository.save(new Role(null, Role.RoleName.ROLE_BUYER));
            log.info("Seeded default roles: {}", roleRepository.count());
        } else {
            log.info("Roles already present: {}", roleRepository.count());
        }

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

        Role adminRole = roleRepository.findByName(Role.RoleName.ROLE_ADMIN)
                .orElseThrow(() -> new IllegalStateException("ROLE_ADMIN must exist before seeding admin accounts"));

        seedAdminUser("Abir-49", "abir-49@bookbridge.local", adminRole);
        seedAdminUser("Ashraful-36", "ashraful-36@bookbridge.local", adminRole);
    }

    private void seedAdminUser(String username, String email, Role adminRole) {
        if (userRepository.existsByUsername(username) || userRepository.existsByEmail(email)) {
            return;
        }

        userRepository.save(User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode("000000"))
                .enabled(true)
                .roles(Set.of(adminRole))
                .build());

        log.info("Seeded admin account: {}", username);
    }

    private void migrateCategorySlugIfNeeded() {
        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            String databaseName = connection.getMetaData().getDatabaseProductName();
            if (databaseName == null || !databaseName.toLowerCase().contains("postgresql")) {
                return;
            }

            String schema = jdbcTemplate.queryForObject("SELECT current_schema()", String.class);
            if (schema == null || schema.isBlank()) {
                schema = "public";
            }

            migrateByteaColumn(schema, "category", "slug", "VARCHAR(255)");
            migrateByteaColumn(schema, "book", "title", "VARCHAR(255)");
            migrateByteaColumn(schema, "book", "author", "VARCHAR(255)");
        } catch (Exception ex) {
            log.warn("Skipping category.slug migration check: {}", ex.getMessage());
        }
    }

    private void migrateByteaColumn(String schema, String tableName, String columnName, String targetSqlType) {
        Integer byteaColumns = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.columns " +
                        "WHERE table_schema = ? AND table_name = ? " +
                        "AND column_name = ? AND data_type = 'bytea'",
                Integer.class,
                schema,
                tableName,
                columnName
        );

        if (byteaColumns == null || byteaColumns == 0) {
            return;
        }

        String qualifiedTable = "\"" + schema + "\".\"" + tableName + "\"";
        String alterSql = "ALTER TABLE " + qualifiedTable +
                " ALTER COLUMN \"" + columnName + "\" TYPE " + targetSqlType +
                " USING convert_from(\"" + columnName + "\", 'UTF8')";
        jdbcTemplate.execute(alterSql);
        log.info("Migrated {}.{} from bytea to {}", tableName, columnName, targetSqlType);
    }
}


