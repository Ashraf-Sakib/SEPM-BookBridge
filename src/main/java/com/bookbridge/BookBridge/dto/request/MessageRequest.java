package com.bookbridge.BookBridge.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MessageRequest {

    @NotNull(message = "Conversation id is required")
    private Integer conversationId;

    @NotNull(message = "Sender id is required")
    private Integer senderId;

    @NotBlank(message = "Message content is required")
    private String content;
}
