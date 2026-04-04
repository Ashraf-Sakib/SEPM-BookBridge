package com.bookbridge.BookBridge.controller;

import com.bookbridge.BookBridge.dto.response.UserResponse;
import com.bookbridge.BookBridge.entity.Message;
import com.bookbridge.BookBridge.service.ConversationService;
import com.bookbridge.BookBridge.service.MessageService;
import com.bookbridge.BookBridge.service.UserService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ConversationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConversationService conversationService;

    @MockBean
    private MessageService messageService;

    @MockBean
    private UserService userService;

    @Test
    void getConversations_shouldReturn400WhenNoFilterProvided() throws Exception {
        mockMvc.perform(get("/api/conversations")
                        .with(user("buyer").roles("BUYER")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getMessages_shouldReturn200ForConversationParticipant() throws Exception {
        UserResponse user = new UserResponse();
        user.setId(2);
        user.setUsername("buyer");

        Message message = new Message();
        message.setId(1L);
        message.setContent("Hello");
        var sender = new com.bookbridge.BookBridge.entity.User();
        sender.setId(2);
        sender.setUsername("buyer");
        message.setSender(sender);
        var conversation = new com.bookbridge.BookBridge.entity.Conversation();
        conversation.setId(7);
        message.setConversation(conversation);

        when(userService.getUserByUsername(eq("buyer"))).thenReturn(user);
        when(messageService.getMessagesByConversationId(7, 2)).thenReturn(List.of(message));

        mockMvc.perform(get("/api/conversations/7/messages")
                        .with(user("buyer").roles("BUYER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("Hello"));
    }

    @Test
    void sendMessage_shouldReturn201ForConversationParticipant() throws Exception {
        UserResponse user = new UserResponse();
        user.setId(2);
        user.setUsername("buyer");

        Message saved = new Message();
        saved.setId(2L);
        saved.setContent("Still available?");
        var sender = new com.bookbridge.BookBridge.entity.User();
        sender.setId(2);
        sender.setUsername("buyer");
        saved.setSender(sender);
        var conversation = new com.bookbridge.BookBridge.entity.Conversation();
        conversation.setId(7);
        saved.setConversation(conversation);

        when(userService.getUserByUsername(eq("buyer"))).thenReturn(user);
        when(messageService.createMessage(eq(7), eq(2), anyString())).thenReturn(saved);

        mockMvc.perform(post("/api/conversations/7/messages")
                        .with(user("buyer").roles("BUYER"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"Still available?\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("Still available?"));
    }
}
