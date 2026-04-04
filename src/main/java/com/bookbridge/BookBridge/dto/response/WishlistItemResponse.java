package com.bookbridge.BookBridge.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WishlistItemResponse {
    private Long id;
    private Integer bookId;
    private String title;
    private String author;
    private Double price;
    private String imageUrl;
    private LocalDateTime savedAt;
}
