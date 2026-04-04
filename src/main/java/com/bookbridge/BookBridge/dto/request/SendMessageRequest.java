package com.bookbridge.BookBridge.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SendMessageRequest {
    @NotBlank(message = "content is required")
    private String content;
}

