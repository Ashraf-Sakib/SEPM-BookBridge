package com.bookbridge.BookBridge.repository;

import com.bookbridge.BookBridge.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByConversationIdOrderByCreatedAtAsc(Integer conversationId);
}