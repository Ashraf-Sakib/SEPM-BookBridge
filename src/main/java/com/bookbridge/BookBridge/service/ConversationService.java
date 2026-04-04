package com.bookbridge.BookBridge.service;

import com.bookbridge.BookBridge.entity.Conversation;
import com.bookbridge.BookBridge.entity.Book;
import com.bookbridge.BookBridge.entity.User;
import com.bookbridge.BookBridge.exception.ResourceNotFoundException;
import com.bookbridge.BookBridge.repository.ConversationRepository;
import com.bookbridge.BookBridge.repository.BookRepository;
import com.bookbridge.BookBridge.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Transactional
    public Conversation createConversation(Integer bookId, Integer userId, String message) {
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new ResourceNotFoundException("Book not found"));
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Conversation conversation = new Conversation();
        conversation.setBook(book);
        conversation.setUser(user);
        conversation.setMessage(message);

        return conversationRepository.save(conversation);
    }

    @Transactional
    public Conversation findOrCreateConversation(Integer bookId, Integer userId, String message) {
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

        return conversationRepository
                .findFirstByUserIdAndBookAddedByIdOrderByUpdatedAtDesc(userId, book.getAddedBy().getId())
                .orElseGet(() -> createConversation(bookId, userId, message));
    }

    @Transactional(readOnly = true)
    public Conversation getConversationById(Integer conversationId) {
        return conversationRepository.findById(conversationId)
            .orElseThrow(() -> new ResourceNotFoundException("Conversation not found"));
    }

    @Transactional(readOnly = true)
    public Page<Conversation> getConversationsByBookId(Integer bookId, int page, int size) {
        return conversationRepository.findByBookId(bookId, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt")));
    }

    @Transactional(readOnly = true)
    public Page<Conversation> getConversationsByUserId(Integer userId, int page, int size) {
        return conversationRepository.findByUserId(userId, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt")));
    }

    @Transactional(readOnly = true)
    public Page<Conversation> getAccessibleConversations(Integer userId, int page, int size) {
        return conversationRepository.findByUserIdOrBookAddedById(
                userId,
                userId,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt")));
    }

    @Transactional
    public Conversation updateConversationResponse(Integer conversationId, String response) {
        Conversation conversation = conversationRepository.findById(conversationId)
            .orElseThrow(() -> new ResourceNotFoundException("Conversation not found"));

        conversation.setResponse(response);
        return conversationRepository.save(conversation);
    }

    @Transactional
    public void deleteConversation(Integer conversationId) {
        if (!conversationRepository.existsById(conversationId)) {
            throw new ResourceNotFoundException("Conversation not found");
        }
        conversationRepository.deleteById(conversationId);
    }
}