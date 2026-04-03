package com.bookbridge.BookBridge.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ConversationRequest {

    @NotNull(message = "bookId is required")
    private Integer bookId;

    @NotNull(message = "userId is required")
    private Integer userId;

    @NotBlank(message = "message is required")
    private String message;
}
