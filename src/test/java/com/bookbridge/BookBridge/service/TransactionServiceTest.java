package com.bookbridge.BookBridge.service;

import com.bookbridge.BookBridge.dto.request.SellBookRequest;
import com.bookbridge.BookBridge.dto.response.TransactionResponse;
import com.bookbridge.BookBridge.entity.Book;
import com.bookbridge.BookBridge.entity.Transaction;
import com.bookbridge.BookBridge.entity.User;
import com.bookbridge.BookBridge.exception.ResourceNotFoundException;
import com.bookbridge.BookBridge.repository.BookRepository;
import com.bookbridge.BookBridge.repository.TransactionRepository;
import com.bookbridge.BookBridge.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TransactionService transactionService;

    private User seller;
    private User buyer;
    private Book book;
    private SellBookRequest sellRequest;

    @BeforeEach
    void setUp() {
        seller = new User();
        seller.setId(1);
        seller.setUsername("seller");
        seller.setEmail("seller@example.com");

        buyer = new User();
        buyer.setId(2);
        buyer.setUsername("buyer");
        buyer.setEmail("buyer@example.com");

        book = new Book();
        book.setId(1);
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setAddedBy(seller);
        book.setAvailable(true);

        sellRequest = new SellBookRequest();
        sellRequest.setBookId(1);
        sellRequest.setBuyerId(2);
        sellRequest.setSalePrice(25.0);
        sellRequest.setNotes("Great condition");
    }

    @Test
    void sellBook_shouldThrowException_whenBookNotFound() {
        when(bookRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            transactionService.sellBook(1, sellRequest);
        });

        verify(bookRepository).findById(1);
    }

    @Test
    void sellBook_shouldThrowException_whenSellerDoesNotOwnBook() {
        when(bookRepository.findById(1)).thenReturn(Optional.of(book));

        assertThrows(IllegalArgumentException.class, () -> {
            transactionService.sellBook(99, sellRequest);
        });
    }

    @Test
    void sellBook_shouldThrowException_whenBuyerNotFound() {
        when(bookRepository.findById(1)).thenReturn(Optional.of(book));
        when(userRepository.findById(2)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            transactionService.sellBook(1, sellRequest);
        });
    }

    @Test
    void sellBook_shouldThrowException_whenBookAlreadySold() {
        book.setAvailable(false);
        when(bookRepository.findById(1)).thenReturn(Optional.of(book));

        assertThrows(IllegalArgumentException.class, () -> transactionService.sellBook(1, sellRequest));
    }

    @Test
    void sellBook_shouldThrowException_whenSellerAndBuyerSame() {
        sellRequest.setBuyerId(1);
        when(bookRepository.findById(1)).thenReturn(Optional.of(book));
        when(userRepository.findById(1)).thenReturn(Optional.of(seller));
        when(transactionRepository.existsByBookId(1)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> transactionService.sellBook(1, sellRequest));
    }

    @Test
    void sellBook_shouldCreateTransaction_whenAllDataIsValid() {
        when(bookRepository.findById(1)).thenReturn(Optional.of(book));
        when(userRepository.findById(1)).thenReturn(Optional.of(seller));
        when(userRepository.findById(2)).thenReturn(Optional.of(buyer));

        Transaction savedTransaction = new Transaction();
        savedTransaction.setId(1);
        savedTransaction.setBook(book);
        savedTransaction.setSeller(seller);
        savedTransaction.setBuyer(buyer);
        savedTransaction.setSalePrice(25.0);
        savedTransaction.setNotes("Great condition");

        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        TransactionResponse response = transactionService.sellBook(1, sellRequest);

        assertNotNull(response);
        assertEquals(1, response.getId());
        assertEquals("Test Book", response.getBookTitle());
        assertEquals("seller", response.getSellerUsername());
        assertEquals("buyer", response.getBuyerUsername());
        assertEquals(25.0, response.getSalePrice());

        verify(transactionRepository).save(any(Transaction.class));
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void getTransactionById_shouldThrowException_whenTransactionNotFound() {
        when(transactionRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            transactionService.getTransactionById(1);
        });
    }

    @Test
    void getTransactionById_shouldReturnTransaction_whenFound() {
        Transaction transaction = new Transaction();
        transaction.setId(1);
        transaction.setBook(book);
        transaction.setSeller(seller);
        transaction.setBuyer(buyer);
        transaction.setSalePrice(25.0);

        when(transactionRepository.findById(1)).thenReturn(Optional.of(transaction));

        TransactionResponse response = transactionService.getTransactionById(1);

        assertNotNull(response);
        assertEquals(1, response.getId());
        assertEquals("Test Book", response.getBookTitle());
    }
}
