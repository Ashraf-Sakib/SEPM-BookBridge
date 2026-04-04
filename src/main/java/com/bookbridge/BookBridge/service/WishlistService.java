package com.bookbridge.BookBridge.service;

import com.bookbridge.BookBridge.dto.response.WishlistItemResponse;
import com.bookbridge.BookBridge.entity.Book;
import com.bookbridge.BookBridge.entity.User;
import com.bookbridge.BookBridge.entity.Wishlist;
import com.bookbridge.BookBridge.exception.ResourceNotFoundException;
import com.bookbridge.BookBridge.repository.BookRepository;
import com.bookbridge.BookBridge.repository.UserRepository;
import com.bookbridge.BookBridge.repository.WishlistRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    @Transactional(readOnly = true)
    public List<WishlistItemResponse> getWishlistByUserId(Integer userId) {
        return wishlistRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void addBook(Integer userId, Integer bookId) {
        if (wishlistRepository.existsByUserIdAndBookId(userId, bookId)) {
            return;
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

        Wishlist wishlist = new Wishlist();
        wishlist.setUser(user);
        wishlist.setBook(book);
        wishlistRepository.save(wishlist);
    }

    @Transactional
    public void removeBook(Integer userId, Integer bookId) {
        if (!wishlistRepository.existsByUserIdAndBookId(userId, bookId)) {
            throw new ResourceNotFoundException("Wishlist item not found");
        }
        wishlistRepository.deleteByUserIdAndBookId(userId, bookId);
    }

    private WishlistItemResponse toResponse(Wishlist wishlist) {
        Book book = wishlist.getBook();
        return new WishlistItemResponse(
                wishlist.getId(),
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getPrice(),
                book.getImageUrl(),
                wishlist.getCreatedAt());
    }
}

