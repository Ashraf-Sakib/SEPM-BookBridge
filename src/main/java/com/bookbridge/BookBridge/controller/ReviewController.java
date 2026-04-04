package com.bookbridge.BookBridge.controller;

import java.util.List;

import com.bookbridge.BookBridge.entity.Book;
import com.bookbridge.BookBridge.entity.Review;
import com.bookbridge.BookBridge.entity.User;
import com.bookbridge.BookBridge.exception.ResourceNotFoundException;
import com.bookbridge.BookBridge.repository.BookRepository;
import com.bookbridge.BookBridge.repository.ReviewRepository;
import com.bookbridge.BookBridge.repository.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public ReviewController(ReviewRepository reviewRepository,
                            UserRepository userRepository,
                            BookRepository bookRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    @GetMapping
    public ResponseEntity<List<Review>> getReviews(
            @RequestParam(required = false) Integer reviewedUserId,
            @RequestParam(required = false) Integer bookId) {
        if (reviewedUserId != null) {
            return ResponseEntity.ok(reviewRepository.findByReviewedUserId(reviewedUserId));
        }
        if (bookId != null) {
            return ResponseEntity.ok(reviewRepository.findByBookId(bookId));
        }
        return ResponseEntity.ok(reviewRepository.findAll());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','BUYER','SELLER')")
    public ResponseEntity<Review> createReview(@Valid @RequestBody CreateReviewRequest request) {
        User reviewer = userRepository.findById(request.getReviewerId())
                .orElseThrow(() -> new ResourceNotFoundException("Reviewer not found"));
        User reviewedUser = userRepository.findById(request.getReviewedUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Reviewed user not found"));
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

        Review review = new Review();
        review.setReviewer(reviewer);
        review.setReviewedUser(reviewedUser);
        review.setBook(book);
        review.setRating(request.getRating());
        review.setComment(request.getComment());

        return ResponseEntity.status(HttpStatus.CREATED).body(reviewRepository.save(review));
    }

    @DeleteMapping("/{reviewId}")
    @PreAuthorize("hasAnyRole('ADMIN','BUYER','SELLER')")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new ResourceNotFoundException("Review not found");
        }
        reviewRepository.deleteById(reviewId);
        return ResponseEntity.noContent().build();
    }

    public static class CreateReviewRequest {
        @NotNull
        private Integer reviewerId;

        @NotNull
        private Integer reviewedUserId;

        @NotNull
        private Integer bookId;

        @NotNull
        @Min(1)
        @Max(5)
        private Integer rating;

        @NotBlank
        private String comment;

        public Integer getReviewerId() {
            return reviewerId;
        }

        public void setReviewerId(Integer reviewerId) {
            this.reviewerId = reviewerId;
        }

        public Integer getReviewedUserId() {
            return reviewedUserId;
        }

        public void setReviewedUserId(Integer reviewedUserId) {
            this.reviewedUserId = reviewedUserId;
        }

        public Integer getBookId() {
            return bookId;
        }

        public void setBookId(Integer bookId) {
            this.bookId = bookId;
        }

        public Integer getRating() {
            return rating;
        }

        public void setRating(Integer rating) {
            this.rating = rating;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }
    }
}
