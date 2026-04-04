package com.bookbridge.BookBridge.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatThreadView {
    private Integer conversationId;
    private String participantName;
    private String previewText;
}