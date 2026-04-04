package com.bookbridge.BookBridge.controller;

import com.bookbridge.BookBridge.dto.response.ChatThreadView;
import com.bookbridge.BookBridge.dto.response.UserResponse;
import com.bookbridge.BookBridge.entity.Book;
import com.bookbridge.BookBridge.entity.Conversation;
import com.bookbridge.BookBridge.entity.Message;
import com.bookbridge.BookBridge.service.BookService;
import com.bookbridge.BookBridge.service.ConversationService;
import com.bookbridge.BookBridge.service.MessageService;
import com.bookbridge.BookBridge.service.UserService;
import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private static final int CONVERSATION_PAGE_SIZE = 100;

    private final ConversationService conversationService;
    private final MessageService messageService;
    private final UserService userService;
    private final BookService bookService;

    @GetMapping({"/chat", "/chat/{conversationId}"})
    public String showChat(
            @PathVariable(required = false) Integer conversationId,
            Principal principal,
            Model model) {

        if (principal == null) {
            return "redirect:/login";
        }

        UserResponse currentUser = userService.getUserByUsername(principal.getName());
        List<Conversation> accessibleConversations = conversationService
                .getAccessibleConversations(currentUser.getId(), 0, CONVERSATION_PAGE_SIZE)
                .getContent();
        List<ChatThreadView> conversations = buildConversationThreads(accessibleConversations, currentUser.getId());

        Conversation activeConversation = conversationId != null
                ? conversationService.getConversationById(conversationId)
            : accessibleConversations.stream().findFirst().orElse(null);

        if (activeConversation != null) {
            boolean canAccess = activeConversation.getUser().getId().equals(currentUser.getId())
                    || activeConversation.getBook().getAddedBy().getId().equals(currentUser.getId());
            if (!canAccess) {
                throw new AccessDeniedException("You do not have access to this conversation");
            }
        }

        if (conversationId == null && activeConversation != null) {
            return "redirect:/chat/" + activeConversation.getId();
        }

        model.addAttribute("conversations", conversations);
        model.addAttribute("conversation", activeConversation);
        model.addAttribute("conversationId", activeConversation != null ? activeConversation.getId() : null);
        model.addAttribute("messages", activeConversation == null
                ? List.<Message>of()
                : messageService.getMessagesByConversationId(activeConversation.getId()));
        model.addAttribute("currentUserId", currentUser.getId());
        model.addAttribute("activeUser", activeConversation == null
                ? "Messages"
                : "Chat with " + resolveParticipantName(activeConversation, currentUser.getId()));
        model.addAttribute("conversations", conversations);

        return "chat";
    }

    @PostMapping("/chat/{conversationId}/message")
    public String sendMessage(
            @PathVariable Integer conversationId,
            @RequestParam String content,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        if (principal == null) {
            return "redirect:/login";
        }

        UserResponse currentUser = userService.getUserByUsername(principal.getName());
        messageService.createMessage(conversationId, currentUser.getId(), content);
        redirectAttributes.addFlashAttribute("successMessage", "Message sent.");
        return "redirect:/chat/" + conversationId;
    }

    @PostMapping("/chat/start")
    public String startConversation(
            @RequestParam Integer bookId,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        if (principal == null) {
            return "redirect:/login";
        }

        UserResponse currentUser = userService.getUserByUsername(principal.getName());
        Book book = bookService.getBookEntityById(bookId);

        if (currentUser.getUsername().equals(book.getAddedBy().getUsername())) {
            redirectAttributes.addFlashAttribute("errorMessage", "You cannot start a chat on your own book.");
            return "redirect:/books";
        }

        Conversation conversation = conversationService.findOrCreateConversation(
                bookId,
                currentUser.getId(),
                "Hi, is this book still available?");

        redirectAttributes.addFlashAttribute("successMessage", "Conversation opened.");
        return "redirect:/chat/" + conversation.getId();
    }

    private List<ChatThreadView> buildConversationThreads(List<Conversation> conversations, Integer currentUserId) {
        Map<Integer, ChatThreadView> threadByParticipant = new LinkedHashMap<>();

        for (Conversation conversation : conversations) {
            Integer participantId = resolveParticipantId(conversation, currentUserId);
            if (participantId == null || threadByParticipant.containsKey(participantId)) {
                continue;
            }

            threadByParticipant.put(participantId, new ChatThreadView(
                    conversation.getId(),
                    resolveParticipantName(conversation, currentUserId),
                    resolvePreviewText(conversation)));
        }

        return List.copyOf(threadByParticipant.values());
    }

    private Integer resolveParticipantId(Conversation conversation, Integer currentUserId) {
        if (conversation.getUser().getId().equals(currentUserId)) {
            return conversation.getBook().getAddedBy().getId();
        }

        if (conversation.getBook().getAddedBy().getId().equals(currentUserId)) {
            return conversation.getUser().getId();
        }

        return null;
    }

    private String resolveParticipantName(Conversation conversation, Integer currentUserId) {
        if (conversation.getUser().getId().equals(currentUserId)) {
            return conversation.getBook().getAddedBy().getUsername();
        }

        if (conversation.getBook().getAddedBy().getId().equals(currentUserId)) {
            return conversation.getUser().getUsername();
        }

        return conversation.getUser().getUsername();
    }

    private String resolvePreviewText(Conversation conversation) {
        return conversation.getResponse() != null ? conversation.getResponse() : conversation.getMessage();
    }
}