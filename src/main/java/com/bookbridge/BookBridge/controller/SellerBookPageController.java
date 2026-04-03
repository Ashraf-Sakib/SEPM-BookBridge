package com.bookbridge.BookBridge.controller;

import com.bookbridge.BookBridge.dto.request.BookCreateRequest;
import com.bookbridge.BookBridge.dto.response.UserResponse;
import com.bookbridge.BookBridge.entity.Book;
import com.bookbridge.BookBridge.service.BookService;
import com.bookbridge.BookBridge.service.UserService;
import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class SellerBookPageController {

    private final BookService bookService;
    private final UserService userService;

    @GetMapping("/seller/books/new")
    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    public String showSellBookForm(Model model) {
        if (!model.containsAttribute("bookCreateRequest")) {
            model.addAttribute("bookCreateRequest", new BookCreateRequest());
        }
        return "books/sell";
    }

    @PostMapping("/seller/books/new")
    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    public String submitSellBookForm(
            @Valid @ModelAttribute("bookCreateRequest") BookCreateRequest request,
            BindingResult result,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "books/sell";
        }

        if (principal == null) {
            return "redirect:/login";
        }

        UserResponse currentUser = userService.getUserByUsername(principal.getName());

        Book book = new Book();
        book.setTitle(request.getTitle().trim());
        book.setAuthor(request.getAuthor().trim());
        book.setDescription(request.getDescription());
        book.setPrice(request.getPrice());
        book.setAvailable(true);

        bookService.createBook(book, currentUser.getId());
        redirectAttributes.addFlashAttribute("successMessage", "Book listed successfully.");
        return "redirect:/books";
    }
}