package com.bookbridge.BookBridge.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {
    private Long id;
    private Integer conversationId;
    private Integer senderId;
    private String senderUsername;
    private String content;
    private LocalDateTime createdAt;
}

