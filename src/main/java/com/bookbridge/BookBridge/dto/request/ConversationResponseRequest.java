package com.bookbridge.BookBridge.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ConversationResponseRequest {

    @NotBlank(message = "response is required")
    private String response;
}
