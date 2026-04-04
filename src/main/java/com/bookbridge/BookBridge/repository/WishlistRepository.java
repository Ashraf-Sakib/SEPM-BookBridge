package com.bookbridge.BookBridge.repository;

import com.bookbridge.BookBridge.entity.Wishlist;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    List<Wishlist> findByUserIdOrderByCreatedAtDesc(Integer userId);
    boolean existsByUserIdAndBookId(Integer userId, Integer bookId);
    void deleteByUserIdAndBookId(Integer userId, Integer bookId);
}
