package com.bookbridge.BookBridge.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminPageControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void adminPage_shouldRenderAdminView_forAdminUser() throws Exception {
        mockMvc.perform(get("/admin").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(view().name("admin"));
    }

    @Test
    void adminPage_shouldRejectNonAdminUsers() throws Exception {
        mockMvc.perform(get("/admin").with(user("buyer").roles("BUYER")))
                .andExpect(status().isForbidden());
    }
}
