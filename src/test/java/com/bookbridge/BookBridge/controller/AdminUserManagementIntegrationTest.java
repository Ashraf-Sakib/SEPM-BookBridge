package com.bookbridge.BookBridge.controller;

import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.bookbridge.BookBridge.dto.response.UserResponse;
import com.bookbridge.BookBridge.service.UserService;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminUserManagementIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void adminUsersPage_shouldRender_forAdminUser() throws Exception {
        UserResponse userResponse = new UserResponse(2, "buyer", "buyer@example.com", true);
        userResponse.setRoles(Set.of("ROLE_BUYER"));

        when(userService.getUsersForAdmin("ALL", "ALL")).thenReturn(List.of(userResponse));

        mockMvc.perform(get("/admin/users").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/users"));
    }

    @Test
    void adminUsersPage_shouldRejectNonAdminUsers() throws Exception {
        mockMvc.perform(get("/admin/users").with(user("buyer").roles("BUYER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateRoles_shouldRedirect_forAdminUser() throws Exception {
        UserResponse updated = new UserResponse(2, "mixed-user", "mixed@example.com", true);
        updated.setRoles(Set.of("ROLE_BUYER", "ROLE_SELLER"));

        when(userService.assignMarketplaceRoles(eq(2), anySet())).thenReturn(updated);

        mockMvc.perform(post("/admin/users/2/roles")
                        .param("roles", "ROLE_BUYER", "ROLE_SELLER")
                        .with(csrf())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"));

        verify(userService).assignMarketplaceRoles(eq(2), anySet());
    }

    @Test
    void disableUser_shouldRedirect_forAdminUser() throws Exception {
        mockMvc.perform(post("/admin/users/2/disable")
                        .with(csrf())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"));

        verify(userService).disableUser(2);
    }

    @Test
    void enableUser_shouldRedirect_forAdminUser() throws Exception {
        mockMvc.perform(post("/admin/users/2/enable")
                        .with(csrf())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"));

        verify(userService).enableUser(2);
    }

    @Test
    void scheduleDelete_shouldRedirect_forAdminUser() throws Exception {
        mockMvc.perform(post("/admin/users/2/schedule-delete")
                        .with(csrf())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"));

        verify(userService).scheduleDeletion(eq(2), eq(14));
    }

    @Test
    void cancelDelete_shouldRedirect_forAdminUser() throws Exception {
        mockMvc.perform(post("/admin/users/2/cancel-delete")
                        .with(csrf())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"));

        verify(userService).cancelScheduledDeletion(2);
    }
}
