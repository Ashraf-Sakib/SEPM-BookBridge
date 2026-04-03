package com.bookbridge.BookBridge.service;

import com.bookbridge.BookBridge.dto.request.SellBookRequest;
import com.bookbridge.BookBridge.dto.response.PageResponse;
import com.bookbridge.BookBridge.dto.response.TransactionResponse;
import com.bookbridge.BookBridge.entity.Book;
import com.bookbridge.BookBridge.entity.Transaction;
import com.bookbridge.BookBridge.entity.User;
import com.bookbridge.BookBridge.exception.ResourceNotFoundException;
import com.bookbridge.BookBridge.repository.BookRepository;
import com.bookbridge.BookBridge.repository.TransactionRepository;
import com.bookbridge.BookBridge.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Transactional
    public TransactionResponse sellBook(Integer sellerId, SellBookRequest request) {
        // Validate book exists and belongs to the seller
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

        if (!book.getAddedBy().getId().equals(sellerId)) {
            throw new IllegalArgumentException("You can only sell books you have added");
        }

        // Validate buyer exists
        User buyer = userRepository.findById(request.getBuyerId())
                .orElseThrow(() -> new ResourceNotFoundException("Buyer not found"));

        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));

        // Create transaction
        Transaction transaction = Transaction.builder()
                .book(book)
                .seller(seller)
                .buyer(buyer)
                .salePrice(request.getSalePrice())
                .notes(request.getNotes())
                .build();

        Transaction savedTransaction = transactionRepository.save(transaction);

        // Mark the book as not available for sale
        book.setAvailable(false);
        bookRepository.save(book);

        return convertToResponse(savedTransaction);
    }

    @Transactional(readOnly = true)
    public PageResponse<TransactionResponse> getTransactionHistory(int page, int size) {
        Page<Transaction> transactions = transactionRepository.findAll(PageRequest.of(page, size));
        return convertPageToResponse(transactions);
    }

    @Transactional(readOnly = true)
    public PageResponse<TransactionResponse> getSellerTransactions(Integer sellerId, int page, int size) {
        if (!userRepository.existsById(sellerId)) {
            throw new ResourceNotFoundException("Seller not found");
        }
        Page<Transaction> transactions = transactionRepository.findBySellerId(sellerId, PageRequest.of(page, size));
        return convertPageToResponse(transactions);
    }

    @Transactional(readOnly = true)
    public PageResponse<TransactionResponse> getBuyerTransactions(Integer buyerId, int page, int size) {
        if (!userRepository.existsById(buyerId)) {
            throw new ResourceNotFoundException("Buyer not found");
        }
        Page<Transaction> transactions = transactionRepository.findByBuyerId(buyerId, PageRequest.of(page, size));
        return convertPageToResponse(transactions);
    }

    @Transactional(readOnly = true)
    public PageResponse<TransactionResponse> getUserTransactions(Integer userId, int page, int size) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found");
        }
        Page<Transaction> transactions = transactionRepository.findBySellerIdOrBuyerId(userId, userId, PageRequest.of(page, size));
        return convertPageToResponse(transactions);
    }

    @Transactional(readOnly = true)
    public TransactionResponse getTransactionById(Integer transactionId) {
        return transactionRepository.findById(transactionId)
                .map(this::convertToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
    }

    @Transactional(readOnly = true)
    public PageResponse<TransactionResponse> getBookTransactionHistory(Integer bookId, int page, int size) {
        if (!bookRepository.existsById(bookId)) {
            throw new ResourceNotFoundException("Book not found");
        }
        Page<Transaction> transactions = transactionRepository.findByBookId(bookId, PageRequest.of(page, size));
        return convertPageToResponse(transactions);
    }

    private TransactionResponse convertToResponse(Transaction transaction) {
        TransactionResponse response = new TransactionResponse();
        response.setId(transaction.getId());
        response.setBookId(transaction.getBook().getId());
        response.setBookTitle(transaction.getBook().getTitle());
        response.setSellerUsername(transaction.getSeller().getUsername());
        response.setBuyerUsername(transaction.getBuyer().getUsername());
        response.setSalePrice(transaction.getSalePrice());
        response.setSalesDateTime(transaction.getSalesDateTime());
        response.setNotes(transaction.getNotes());
        response.setCreatedAt(transaction.getCreatedAt());
        return response;
    }

    private PageResponse<TransactionResponse> convertPageToResponse(Page<Transaction> pageContent) {
        PageResponse<TransactionResponse> pageResponse = new PageResponse<>();
        pageResponse.setContent(pageContent.getContent().stream()
                .map(this::convertToResponse)
                .toList());
        pageResponse.setPageNumber(pageContent.getNumber());
        pageResponse.setPageSize(pageContent.getSize());
        pageResponse.setTotalElements(pageContent.getTotalElements());
        pageResponse.setTotalPages(pageContent.getTotalPages());
        pageResponse.setLast(pageContent.isLast());
        pageResponse.setFirst(pageContent.isFirst());
        return pageResponse;
    }
}
