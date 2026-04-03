package com.bookbridge.BookBridge.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import com.bookbridge.BookBridge.entity.Book;

public interface BookRepository extends JpaRepository<Book, Integer> {
    Page<Book> findByTitleContainingIgnoreCase(String title, PageRequest pageRequest);
    Page<Book> findByAuthorContainingIgnoreCase(String author, PageRequest pageRequest);
}
