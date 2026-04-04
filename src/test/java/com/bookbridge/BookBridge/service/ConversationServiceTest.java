package com.bookbridge.BookBridge.service;

import com.bookbridge.BookBridge.entity.Book;
import com.bookbridge.BookBridge.entity.Conversation;
import com.bookbridge.BookBridge.entity.User;
import com.bookbridge.BookBridge.repository.BookRepository;
import com.bookbridge.BookBridge.repository.ConversationRepository;
import com.bookbridge.BookBridge.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConversationServiceTest {

    @Mock
    private ConversationRepository conversationRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ConversationService conversationService;

    @Test
    void findOrCreateConversation_shouldReuseExistingConversationForSameSellerAndBuyer() {
        User buyer = new User();
        buyer.setId(1);

        User seller = new User();
        seller.setId(2);

        Book book = new Book();
        book.setId(10);
        book.setAddedBy(seller);

        Conversation existingConversation = new Conversation();
        existingConversation.setId(99);
        existingConversation.setBook(book);
        existingConversation.setUser(buyer);

        when(bookRepository.findById(10)).thenReturn(Optional.of(book));
        when(conversationRepository.findFirstByUserIdAndBookAddedByIdOrderByUpdatedAtDesc(1, 2))
                .thenReturn(Optional.of(existingConversation));

        Conversation result = conversationService.findOrCreateConversation(10, 1, "Hello");

        assertSame(existingConversation, result);
        verify(conversationRepository, never()).save(any(Conversation.class));
        verify(userRepository, never()).findById(anyInt());
    }
}