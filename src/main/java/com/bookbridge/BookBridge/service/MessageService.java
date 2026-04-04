package com.bookbridge.BookBridge.service;

import com.bookbridge.BookBridge.entity.Conversation;
import com.bookbridge.BookBridge.entity.Message;
import com.bookbridge.BookBridge.entity.User;
import com.bookbridge.BookBridge.exception.ResourceNotFoundException;
import com.bookbridge.BookBridge.repository.ConversationRepository;
import com.bookbridge.BookBridge.repository.MessageRepository;
import com.bookbridge.BookBridge.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<Message> getMessagesByConversationId(Integer conversationId) {
        return messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);
    }

    @Transactional(readOnly = true)
    public List<Message> getMessagesByConversationId(Integer conversationId, Integer requesterId) {
        getConversationAndValidateAccess(conversationId, requesterId);
        return messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);
    }

    @Transactional
    public Message createMessage(Integer conversationId, Integer senderId, String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Message content is required");
        }

        Conversation conversation = getConversationAndValidateAccess(conversationId, senderId);
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Message message = new Message();
        message.setConversation(conversation);
        message.setSender(sender);
        message.setContent(content.trim());

        conversation.setUpdatedAt(LocalDateTime.now());
        return messageRepository.save(message);
    }

    private Conversation getConversationAndValidateAccess(Integer conversationId, Integer userId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found"));

        Integer buyerId = conversation.getUser().getId();
        Integer sellerId = conversation.getBook().getAddedBy().getId();
        if (!userId.equals(buyerId) && !userId.equals(sellerId)) {
            throw new AccessDeniedException("You do not have access to this conversation");
        }

        return conversation;
    }
}