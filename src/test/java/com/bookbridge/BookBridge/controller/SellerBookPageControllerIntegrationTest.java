package com.bookbridge.BookBridge.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bookbridge.BookBridge.dto.response.UserResponse;
import com.bookbridge.BookBridge.entity.Book;
import com.bookbridge.BookBridge.service.BookService;
import com.bookbridge.BookBridge.service.UserService;
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
class SellerBookPageControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private UserService userService;

    @Test
    void showSellBookForm_shouldReturn200_forSeller() throws Exception {
        mockMvc.perform(get("/seller/books/new")
                        .with(user("seller").roles("SELLER")))
                .andExpect(status().isOk());
    }

    @Test
    void submitSellBookForm_shouldRedirectToBooks_whenValid() throws Exception {
        when(userService.getUserByUsername("seller"))
                .thenReturn(new UserResponse(1, "seller", "seller@test.com", true));

        mockMvc.perform(post("/seller/books/new")
                        .with(user("seller").roles("SELLER"))
                        .with(csrf())
                        .param("title", "Clean Code")
                        .param("author", "Robert C. Martin")
                        .param("price", "12.50")
                        .param("description", "Second-hand, good condition"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));

        verify(bookService).createBook(any(Book.class), eq(1));
    }
}