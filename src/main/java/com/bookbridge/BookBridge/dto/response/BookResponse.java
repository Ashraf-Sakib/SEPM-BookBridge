package com.bookbridge.BookBridge.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookResponse {
    private Integer id;
    private String title;
    private String description;
    private String author;
    private String isbn;
    private String publisher;
    private Integer publishYear;
    private String imageUrl;
    private Double rating;
    private Integer totalPages;
    private String addedBy;
    private LocalDateTime createdAt;
}