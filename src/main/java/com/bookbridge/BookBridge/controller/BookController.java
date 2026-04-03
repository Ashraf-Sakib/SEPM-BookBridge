package com.bookbridge.BookBridge.controller;

import com.bookbridge.BookBridge.dto.response.BookResponse;
import com.bookbridge.BookBridge.dto.response.PageResponse;
import com.bookbridge.BookBridge.entity.Book;
import com.bookbridge.BookBridge.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    public ResponseEntity<BookResponse> createBook(@Valid @RequestBody Book book,
                                                   @RequestParam Integer userId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookService.createBook(book, userId));
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<BookResponse> getBookById(@PathVariable Integer bookId) {
        return ResponseEntity.ok(bookService.getBookById(bookId));
    }

    @GetMapping
    public ResponseEntity<PageResponse<BookResponse>> getAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author) {

        if (title != null && !title.isBlank()) {
            return ResponseEntity.ok(bookService.searchBooksByTitle(title, page, size));
        }
        if (author != null && !author.isBlank()) {
            return ResponseEntity.ok(bookService.searchBooksByAuthor(author, page, size));
        }
        return ResponseEntity.ok(bookService.getAllBooks(page, size));
    }

    @PutMapping("/{bookId}")
    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    public ResponseEntity<BookResponse> updateBook(@PathVariable Integer bookId,
                                                   @RequestBody Book bookDetails) {
        return ResponseEntity.ok(bookService.updateBook(bookId, bookDetails));
    }

    @DeleteMapping("/{bookId}")
    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    public ResponseEntity<Void> deleteBook(@PathVariable Integer bookId) {
        bookService.deleteBook(bookId);
        return ResponseEntity.noContent().build();
    }
}
