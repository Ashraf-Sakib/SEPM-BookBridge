package com.bookbridge.BookBridge.repository;

import java.util.List;

import com.bookbridge.BookBridge.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByReviewedUserId(Integer reviewedUserId);
    List<Review> findByBookId(Integer bookId);
}
