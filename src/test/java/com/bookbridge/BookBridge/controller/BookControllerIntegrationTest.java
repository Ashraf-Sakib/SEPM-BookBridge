package com.bookbridge.BookBridge.controller;

import com.bookbridge.BookBridge.dto.response.BookResponse;
import com.bookbridge.BookBridge.dto.response.PageResponse;
import com.bookbridge.BookBridge.entity.Book;
import com.bookbridge.BookBridge.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BookControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookService bookService;

    @Test
    void createBook_shouldReturn201() throws Exception {
        Book request = new Book();
        request.setTitle("Domain-Driven Design");

        BookResponse response = new BookResponse();
        response.setId(1);
        response.setTitle("Domain-Driven Design");

        when(bookService.createBook(any(Book.class), eq(1))).thenReturn(response);

        mockMvc.perform(post("/api/books?userId=1")
                        .with(user("seller").roles("SELLER"))
                .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void browseBooksPage_shouldReturn200AndRenderListView() throws Exception {
        BookResponse bookResponse = new BookResponse();
        bookResponse.setId(1);
        bookResponse.setTitle("Clean Code");
        bookResponse.setAuthor("Robert C. Martin");

        PageResponse<BookResponse> pageResponse = new PageResponse<>();
        pageResponse.setContent(List.of(bookResponse));
        pageResponse.setPageNumber(0);
        pageResponse.setPageSize(12);
        pageResponse.setTotalPages(1);
        pageResponse.setFirst(true);
        pageResponse.setLast(true);

        when(bookService.getAllBooks(0, 12)).thenReturn(pageResponse);

        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/list"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Browse Books")));
    }
}
