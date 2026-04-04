package com.bookbridge.BookBridge.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

import com.bookbridge.BookBridge.entity.Conversation;

public interface ConversationRepository extends JpaRepository<Conversation, Integer> {
    Page<Conversation> findByBookId(Integer bookId, PageRequest pageRequest);
    Page<Conversation> findByUserId(Integer userId, PageRequest pageRequest);
    Page<Conversation> findByUserIdOrBookAddedById(Integer userId, Integer addedById, PageRequest pageRequest);
    Optional<Conversation> findFirstByBookIdAndUserIdOrderByCreatedAtDesc(Integer bookId, Integer userId);
    Optional<Conversation> findFirstByUserIdAndBookAddedByIdOrderByUpdatedAtDesc(Integer userId, Integer addedById);
}
