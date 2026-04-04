package com.bookbridge.BookBridge.controller;

import com.bookbridge.BookBridge.dto.request.ConversationRequest;
import com.bookbridge.BookBridge.dto.request.ConversationResponseRequest;
import com.bookbridge.BookBridge.dto.request.SendMessageRequest;
import com.bookbridge.BookBridge.dto.response.ConversationResponse;
import com.bookbridge.BookBridge.dto.response.MessageResponse;
import com.bookbridge.BookBridge.entity.Conversation;
import com.bookbridge.BookBridge.entity.Message;
import com.bookbridge.BookBridge.service.ConversationService;
import com.bookbridge.BookBridge.service.MessageService;
import com.bookbridge.BookBridge.service.UserService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
public class ConversationController {

    private final ConversationService conversationService;
    private final MessageService messageService;
    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','BUYER','SELLER')")
    public ResponseEntity<ConversationResponse> createConversation(
            @Valid @RequestBody ConversationRequest request) {
        Conversation conversation = conversationService.createConversation(
                request.getBookId(),
                request.getUserId(),
                request.getMessage());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(conversation));
    }

    @GetMapping("/{conversationId}")
    @PreAuthorize("hasAnyRole('ADMIN','BUYER','SELLER')")
    public ResponseEntity<ConversationResponse> getConversationById(@PathVariable Integer conversationId) {
        return ResponseEntity.ok(mapToResponse(conversationService.getConversationById(conversationId)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','BUYER','SELLER')")
    public ResponseEntity<Page<ConversationResponse>> getConversations(
            @RequestParam(required = false) Integer bookId,
            @RequestParam(required = false) Integer userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (bookId != null) {
            return ResponseEntity.ok(
                    conversationService.getConversationsByBookId(bookId, page, size)
                            .map(this::mapToResponse));
        }

        if (userId != null) {
            return ResponseEntity.ok(
                    conversationService.getConversationsByUserId(userId, page, size)
                            .map(this::mapToResponse));
        }

        throw new IllegalArgumentException("Either bookId or userId must be provided");
    }

    @PatchMapping("/{conversationId}/response")
    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    public ResponseEntity<ConversationResponse> updateConversationResponse(
            @PathVariable Integer conversationId,
            @Valid @RequestBody ConversationResponseRequest request) {
        Conversation conversation = conversationService.updateConversationResponse(
                conversationId,
                request.getResponse());
        return ResponseEntity.ok(mapToResponse(conversation));
    }

    @DeleteMapping("/{conversationId}")
    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    public ResponseEntity<Void> deleteConversation(@PathVariable Integer conversationId) {
        conversationService.deleteConversation(conversationId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{conversationId}/messages")
    @PreAuthorize("hasAnyRole('ADMIN','BUYER','SELLER')")
    public ResponseEntity<List<MessageResponse>> getMessages(
            @PathVariable Integer conversationId,
            Principal principal) {
        Integer currentUserId = userService.getUserByUsername(principal.getName()).getId();
        List<MessageResponse> messages = messageService.getMessagesByConversationId(conversationId, currentUserId)
                .stream()
                .map(this::mapToMessageResponse)
                .toList();
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/{conversationId}/messages")
    @PreAuthorize("hasAnyRole('ADMIN','BUYER','SELLER')")
    public ResponseEntity<MessageResponse> sendMessage(
            @PathVariable Integer conversationId,
            @Valid @RequestBody SendMessageRequest request,
            Principal principal) {
        Integer currentUserId = userService.getUserByUsername(principal.getName()).getId();
        Message saved = messageService.createMessage(conversationId, currentUserId, request.getContent());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToMessageResponse(saved));
    }

    private ConversationResponse mapToResponse(Conversation conversation) {
        return new ConversationResponse(
                conversation.getId(),
                conversation.getBook().getId(),
                conversation.getUser().getId(),
                conversation.getUser().getUsername(),
                conversation.getMessage(),
                conversation.getResponse(),
                conversation.getCreatedAt());
    }

    private MessageResponse mapToMessageResponse(Message message) {
        return new MessageResponse(
                message.getId(),
                message.getConversation().getId(),
                message.getSender().getId(),
                message.getSender().getUsername(),
                message.getContent(),
                message.getCreatedAt());
    }
}
