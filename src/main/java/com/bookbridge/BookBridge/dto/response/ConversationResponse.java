package com.bookbridge.BookBridge.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversationResponse {
    private Integer id;
    private Integer bookId;
    private Integer userId;
    private String username;
    private String message;
    private String response;
    private LocalDateTime createdAt;
}
