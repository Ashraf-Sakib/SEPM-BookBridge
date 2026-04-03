package com.bookbridge.BookBridge.service;

import com.bookbridge.BookBridge.dto.response.BookResponse;
import com.bookbridge.BookBridge.dto.response.PageResponse;
import com.bookbridge.BookBridge.entity.Book;
import com.bookbridge.BookBridge.entity.User;
import com.bookbridge.BookBridge.exception.ResourceNotFoundException;
import com.bookbridge.BookBridge.repository.BookRepository;
import com.bookbridge.BookBridge.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Transactional
    public BookResponse createBook(Book book, Integer userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        book.setAddedBy(user);
        Book savedBook = bookRepository.save(book);
        return convertToResponse(savedBook);
    }

    @Transactional(readOnly = true)
    public BookResponse getBookById(Integer bookId) {
        return bookRepository.findById(bookId)
                .map(this::convertToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));
    }

    @Transactional(readOnly = true)
    public PageResponse<BookResponse> getAllBooks(int page, int size) {
        Page<Book> books = bookRepository.findAll(PageRequest.of(page, size));
        return convertPageToResponse(books);
    }

    @Transactional(readOnly = true)
    public PageResponse<BookResponse> searchBooksByTitle(String title, int page, int size) {
        Page<Book> books = bookRepository.findByTitleContainingIgnoreCase(title, PageRequest.of(page, size));
        return convertPageToResponse(books);
    }

    @Transactional(readOnly = true)
    public PageResponse<BookResponse> searchBooksByAuthor(String author, int page, int size) {
        Page<Book> books = bookRepository.findByAuthorContainingIgnoreCase(author, PageRequest.of(page, size));
        return convertPageToResponse(books);
    }

    @Transactional
    public BookResponse updateBook(Integer bookId, Book bookDetails) {
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

        if (bookDetails.getTitle() != null) book.setTitle(bookDetails.getTitle());
        if (bookDetails.getDescription() != null) book.setDescription(bookDetails.getDescription());
        if (bookDetails.getAuthor() != null) book.setAuthor(bookDetails.getAuthor());
        if (bookDetails.getIsbn() != null) book.setIsbn(bookDetails.getIsbn());
        if (bookDetails.getPublisher() != null) book.setPublisher(bookDetails.getPublisher());
        if (bookDetails.getPublishYear() != null) book.setPublishYear(bookDetails.getPublishYear());
        if (bookDetails.getImageUrl() != null) book.setImageUrl(bookDetails.getImageUrl());
        if (bookDetails.getRating() != null) book.setRating(bookDetails.getRating());
        if (bookDetails.getTotalPages() != null) book.setTotalPages(bookDetails.getTotalPages());

        Book updatedBook = bookRepository.save(book);
        return convertToResponse(updatedBook);
    }

    @Transactional
    public void deleteBook(Integer bookId) {
        if (!bookRepository.existsById(bookId)) {
            throw new ResourceNotFoundException("Book not found");
        }
        bookRepository.deleteById(bookId);
    }

    private BookResponse convertToResponse(Book book) {
        BookResponse response = new BookResponse();
        response.setId(book.getId());
        response.setTitle(book.getTitle());
        response.setDescription(book.getDescription());
        response.setAuthor(book.getAuthor());
        response.setIsbn(book.getIsbn());
        response.setPublisher(book.getPublisher());
        response.setPublishYear(book.getPublishYear());
        response.setImageUrl(book.getImageUrl());
        response.setRating(book.getRating());
        response.setTotalPages(book.getTotalPages());
        response.setAddedBy(book.getAddedBy().getUsername());
        response.setCreatedAt(book.getCreatedAt());
        return response;
    }

    private PageResponse<BookResponse> convertPageToResponse(Page<Book> page) {
        PageResponse<BookResponse> response = new PageResponse<>();
        response.setContent(page.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList()));
        response.setPageNumber(page.getNumber());
        response.setPageSize(page.getSize());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setFirst(page.isFirst());
        response.setLast(page.isLast());
        return response;
    }
}