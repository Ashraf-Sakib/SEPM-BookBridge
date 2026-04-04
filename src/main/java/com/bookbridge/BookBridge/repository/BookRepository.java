package com.bookbridge.BookBridge.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

import com.bookbridge.BookBridge.entity.Book;

public interface BookRepository extends JpaRepository<Book, Integer> {
    @Query("SELECT DISTINCT b FROM Book b LEFT JOIN FETCH b.addedBy WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    Page<Book> findByTitleContainingIgnoreCase(@Param("title") String title, Pageable pageable);

    @Query("SELECT DISTINCT b FROM Book b LEFT JOIN FETCH b.addedBy WHERE LOWER(b.author) LIKE LOWER(CONCAT('%', :author, '%'))")
    Page<Book> findByAuthorContainingIgnoreCase(@Param("author") String author, Pageable pageable);

    @Query("SELECT DISTINCT b FROM Book b LEFT JOIN FETCH b.addedBy LEFT JOIN b.category c WHERE LOWER(c.slug) = LOWER(:slug)")
    Page<Book> findByCategory_Slug(@Param("slug") String slug, Pageable pageable);

    @Query("SELECT DISTINCT b FROM Book b LEFT JOIN FETCH b.addedBy LEFT JOIN b.category c " +
            "WHERE (:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))) " +
            "AND (:author IS NULL OR LOWER(b.author) LIKE LOWER(CONCAT('%', :author, '%'))) " +
            "AND (:categorySlug IS NULL OR LOWER(c.slug) = LOWER(:categorySlug))")
    Page<Book> findByFilters(
            @Param("title") String title,
            @Param("author") String author,
            @Param("categorySlug") String categorySlug,
            Pageable pageable);

    @Query("SELECT b FROM Book b LEFT JOIN FETCH b.addedBy WHERE b.addedBy.id = :addedById ORDER BY b.createdAt DESC")
    List<Book> findByAddedByIdOrderByCreatedAtDesc(@Param("addedById") Integer addedById);
}
