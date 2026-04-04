package com.bookbridge.BookBridge.entity;

import java.time.LocalDateTime;

import com.bookbridge.BookBridge.entity.enums.BookCondition;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "book")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    private String author;
    private String isbn;
    private String publisher;
    private Integer publishYear;
    private String imageUrl;
    private Double rating;
    private Integer totalPages;
    private Double price;
    private Double discountPercentage;
    private Boolean available = true;

    @Enumerated(EnumType.STRING)
    private BookCondition condition;

    @ManyToOne
    @JoinColumn(name = "added_by", nullable = false)
    private User addedBy;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
