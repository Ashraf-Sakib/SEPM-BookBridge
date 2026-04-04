package com.bookbridge.BookBridge.controller;

import com.bookbridge.BookBridge.dto.response.UserResponse;
import com.bookbridge.BookBridge.dto.response.WishlistItemResponse;
import com.bookbridge.BookBridge.service.UserService;
import com.bookbridge.BookBridge.service.WishlistService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
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
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private WishlistService wishlistService;

    @Test
    void getUserById_shouldReturn200() throws Exception {
        when(userService.getUserById(1)).thenReturn(new UserResponse(1, "buyer1", "buyer1@example.com", true));

        mockMvc.perform(get("/api/users/1").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("buyer1"));
    }

    @Test
    void getMyWishlist_shouldReturn200() throws Exception {
        when(userService.getUserByUsername(eq("buyer")))
                .thenReturn(new UserResponse(2, "buyer", "buyer@example.com", true));
        when(wishlistService.getWishlistByUserId(2))
                .thenReturn(List.of(new WishlistItemResponse(
                        1L,
                        10,
                        "Clean Code",
                        "Robert C. Martin",
                        20.0,
                        "/uploads/clean-code.jpg",
                        LocalDateTime.now())));

        mockMvc.perform(get("/api/users/me/wishlist")
                        .with(user("buyer").roles("BUYER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bookId").value(10));
    }

    @Test
    void saveBookToWishlist_shouldReturn200() throws Exception {
        when(userService.getUserByUsername(eq("buyer")))
                .thenReturn(new UserResponse(2, "buyer", "buyer@example.com", true));
        doNothing().when(wishlistService).addBook(2, 10);

        mockMvc.perform(post("/api/users/me/wishlist/10")
                        .with(user("buyer").roles("BUYER"))
                        .with(csrf()))
                .andExpect(status().isOk());
    }
}
