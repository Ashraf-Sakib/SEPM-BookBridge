package com.bookbridge.BookBridge.controller;

import com.bookbridge.BookBridge.dto.response.UserResponse;
import com.bookbridge.BookBridge.entity.Conversation;
import com.bookbridge.BookBridge.entity.Message;
import com.bookbridge.BookBridge.service.ConversationService;
import com.bookbridge.BookBridge.service.MessageService;
import com.bookbridge.BookBridge.service.UserService;
import java.security.Principal;
import java.util.List;
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

    @GetMapping({"/chat", "/chat/{conversationId}"})
    public String showChat(
            @PathVariable(required = false) Integer conversationId,
            Principal principal,
            Model model) {

        if (principal == null) {
            return "redirect:/login";
        }

        UserResponse currentUser = userService.getUserByUsername(principal.getName());
        List<Conversation> conversations = conversationService
                .getAccessibleConversations(currentUser.getId(), 0, CONVERSATION_PAGE_SIZE)
                .getContent();

        Conversation activeConversation = conversationId != null
                ? conversationService.getConversationById(conversationId)
                : conversations.stream().findFirst().orElse(null);

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
                : activeConversation.getBook().getTitle());

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
}