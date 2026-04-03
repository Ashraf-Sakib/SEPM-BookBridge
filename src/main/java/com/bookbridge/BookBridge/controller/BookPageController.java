package com.bookbridge.BookBridge.controller;

import com.bookbridge.BookBridge.dto.response.BookResponse;
import com.bookbridge.BookBridge.dto.response.PageResponse;
import com.bookbridge.BookBridge.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class BookPageController {

    private final BookService bookService;

    @GetMapping("/books")
    public String browseBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            Model model) {

        int safePage = Math.max(page, 0);
        int safeSize = Math.max(1, Math.min(size, 50));

        PageResponse<BookResponse> response;
        if (title != null && !title.isBlank()) {
            response = bookService.searchBooksByTitle(title, safePage, safeSize);
        } else if (author != null && !author.isBlank()) {
            response = bookService.searchBooksByAuthor(author, safePage, safeSize);
        } else {
            response = bookService.getAllBooks(safePage, safeSize);
        }

        model.addAttribute("books", response.getContent());
        model.addAttribute("pageNumber", response.getPageNumber());
        model.addAttribute("totalPages", response.getTotalPages());
        model.addAttribute("first", response.isFirst());
        model.addAttribute("last", response.isLast());
        model.addAttribute("size", safeSize);
        model.addAttribute("title", title);
        model.addAttribute("author", author);

        return "books/list";
    }
}
