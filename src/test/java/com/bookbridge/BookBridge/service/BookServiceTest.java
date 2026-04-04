package com.bookbridge.BookBridge.service;

import com.bookbridge.BookBridge.dto.response.BookResponse;
import com.bookbridge.BookBridge.dto.response.PageResponse;
import com.bookbridge.BookBridge.entity.Book;
import com.bookbridge.BookBridge.entity.User;
import com.bookbridge.BookBridge.exception.ResourceNotFoundException;
import com.bookbridge.BookBridge.repository.BookRepository;
import com.bookbridge.BookBridge.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookService bookService;

    private User user;
    private Book book;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setUsername("seller1");

        book = new Book();
        book.setId(10);
        book.setTitle("Spring In Action");
        book.setAuthor("Craig");
        book.setAddedBy(user);
    }

    @Test
    void createBook_shouldSetAddedByAndReturnResponse() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookResponse response = bookService.createBook(book, 1);

        assertEquals("seller1", response.getAddedBy());
        verify(bookRepository).save(book);
    }

    @Test
    void getBookById_shouldReturnResponse() {
        when(bookRepository.findById(10)).thenReturn(Optional.of(book));

        BookResponse response = bookService.getBookById(10);

        assertEquals("Spring In Action", response.getTitle());
    }

    @Test
    void getBookById_shouldThrowWhenMissing() {
        when(bookRepository.findById(10)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookService.getBookById(10));
    }

    @Test
    void getAllBooks_shouldReturnPageResponse() {
        Page<Book> page = new PageImpl<>(List.of(book), PageRequest.of(0, 10), 1);
        when(bookRepository.findAll(PageRequest.of(0, 10))).thenReturn(page);

        PageResponse<BookResponse> response = bookService.getAllBooks(0, 10);

        assertEquals(1, response.getTotalElements());
        assertEquals("Spring In Action", response.getContent().get(0).getTitle());
    }

    @Test
    void deleteBook_shouldDeleteWhenExists() {
        when(bookRepository.existsById(10)).thenReturn(true);

        bookService.deleteBook(10);

        verify(bookRepository).deleteById(10);
    }

    @Test
    void convertToResponse_shouldHandleNullAddedBy() {
        Book bookWithoutSeller = new Book();
        bookWithoutSeller.setId(11);
        bookWithoutSeller.setTitle("Test Book");
        bookWithoutSeller.setAuthor("Test Author");
        bookWithoutSeller.setAddedBy(null);

        when(bookRepository.findById(11)).thenReturn(Optional.of(bookWithoutSeller));

        BookResponse response = bookService.getBookById(11);

        // This should not throw NPE and addedBy should be null
        assertNull(response.getAddedBy());
    }
}
