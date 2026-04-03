package com.bookbridge.BookBridge.controller;

import com.bookbridge.BookBridge.dto.request.SellBookRequest;
import com.bookbridge.BookBridge.dto.response.TransactionResponse;
import com.bookbridge.BookBridge.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.containsString;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TransactionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TransactionService transactionService;

    @Test
    void sellBook_shouldReturn201_whenValidRequest() throws Exception {
        SellBookRequest request = new SellBookRequest();
        request.setBookId(1);
        request.setBuyerId(2);
        request.setSalePrice(25.0);
        request.setNotes("Great condition");

        TransactionResponse response = new TransactionResponse();
        response.setId(1);
        response.setBookId(1);
        response.setBookTitle("Test Book");
        response.setSellerUsername("seller");
        response.setBuyerUsername("buyer");
        response.setSalePrice(25.0);

        when(transactionService.sellBook(eq(1), any(SellBookRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/transactions/sell?sellerId=1")
                        .with(user("seller").roles("SELLER"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("Test Book")));
    }

    @Test
    void getTransactionHistory_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/transactions")
                        .with(user("user").roles("USER")))
                .andExpect(status().isOk());
    }

    @Test
    void getSellerTransactions_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/transactions/seller/1")
                        .with(user("seller").roles("SELLER")))
                .andExpect(status().isOk());
    }

    @Test
    void getBuyerTransactions_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/transactions/buyer/2")
                        .with(user("buyer").roles("USER")))
                .andExpect(status().isOk());
    }

    @Test
    void getUserTransactions_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/transactions/user/1")
                        .with(user("user").roles("USER")))
                .andExpect(status().isOk());
    }

    @Test
    void getBookTransactionHistory_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/transactions/book/1")
                        .with(user("user").roles("USER")))
                .andExpect(status().isOk());
    }
}
