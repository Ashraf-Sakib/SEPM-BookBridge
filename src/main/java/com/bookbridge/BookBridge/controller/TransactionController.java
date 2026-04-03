package com.bookbridge.BookBridge.controller;

import com.bookbridge.BookBridge.dto.request.SellBookRequest;
import com.bookbridge.BookBridge.dto.response.PageResponse;
import com.bookbridge.BookBridge.dto.response.TransactionResponse;
import com.bookbridge.BookBridge.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/sell")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<TransactionResponse> sellBook(
            @RequestParam Integer sellerId,
            @Valid @RequestBody SellBookRequest request) {
        TransactionResponse response = transactionService.sellBook(sellerId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<PageResponse<TransactionResponse>> getTransactionHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(transactionService.getTransactionHistory(page, size));
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponse> getTransactionById(@PathVariable Integer transactionId) {
        return ResponseEntity.ok(transactionService.getTransactionById(transactionId));
    }

    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<PageResponse<TransactionResponse>> getSellerTransactions(
            @PathVariable Integer sellerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(transactionService.getSellerTransactions(sellerId, page, size));
    }

    @GetMapping("/buyer/{buyerId}")
    public ResponseEntity<PageResponse<TransactionResponse>> getBuyerTransactions(
            @PathVariable Integer buyerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(transactionService.getBuyerTransactions(buyerId, page, size));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<PageResponse<TransactionResponse>> getUserTransactions(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(transactionService.getUserTransactions(userId, page, size));
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<PageResponse<TransactionResponse>> getBookTransactionHistory(
            @PathVariable Integer bookId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(transactionService.getBookTransactionHistory(bookId, page, size));
    }
}
