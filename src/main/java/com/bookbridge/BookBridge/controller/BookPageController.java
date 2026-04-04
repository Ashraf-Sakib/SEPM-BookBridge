package com.bookbridge.BookBridge.controller;

import com.bookbridge.BookBridge.dto.response.BookResponse;
import com.bookbridge.BookBridge.dto.response.PageResponse;
import com.bookbridge.BookBridge.service.BookService;
import com.bookbridge.BookBridge.service.CategoryService;
import com.bookbridge.BookBridge.service.UserService;
import com.bookbridge.BookBridge.service.WishlistService;
import java.security.Principal;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class BookPageController {

    private final BookService bookService;
    private final CategoryService categoryService;
    private final UserService userService;
    private final WishlistService wishlistService;

    @GetMapping("/books")
    public String browseBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String category,
            Principal principal,
            Model model) {

        int safePage = Math.max(page, 0);
        int safeSize = Math.max(1, Math.min(size, 50));

        PageResponse<BookResponse> response = bookService.getBooksByFilters(title, author, category, safePage, safeSize);

        model.addAttribute("books", response.getContent());
        model.addAttribute("pageNumber", response.getPageNumber());
        model.addAttribute("totalPages", response.getTotalPages());
        model.addAttribute("totalElements", response.getTotalElements());
        model.addAttribute("first", response.isFirst());
        model.addAttribute("last", response.isLast());
        model.addAttribute("size", safeSize);
        model.addAttribute("title", title);
        model.addAttribute("author", author);
        model.addAttribute("category", category);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("hasFilters", (title != null && !title.isBlank())
                || (author != null && !author.isBlank())
                || (category != null && !category.isBlank()));
        model.addAttribute("wishlistBookIds", Set.<Integer>of());

        if (principal != null) {
            var currentUser = userService.getUserByUsername(principal.getName());
            model.addAttribute("currentUsername", currentUser.getUsername());
            model.addAttribute("myBooks", bookService.getBooksAddedByUser(currentUser.getId()));
            model.addAttribute("wishlistBookIds", wishlistService.getWishlistByUserId(currentUser.getId())
                    .stream()
                    .map(item -> item.getBookId())
                    .collect(java.util.stream.Collectors.toSet()));
        }

        return "books/list";
    }

    @PostMapping("/books/{bookId}/wishlist/save")
    public String saveToWishlist(
            @PathVariable Integer bookId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        if (principal == null) {
            return "redirect:/login";
        }

        Integer userId = userService.getUserByUsername(principal.getName()).getId();
        wishlistService.addBook(userId, bookId);
        redirectAttributes.addFlashAttribute("successMessage", "Book saved to wishlist.");
        return buildBooksRedirect(title, author, category, page, size);
    }

    @PostMapping("/books/{bookId}/wishlist/remove")
    public String removeFromWishlist(
            @PathVariable Integer bookId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        if (principal == null) {
            return "redirect:/login";
        }

        Integer userId = userService.getUserByUsername(principal.getName()).getId();
        wishlistService.removeBook(userId, bookId);
        redirectAttributes.addFlashAttribute("successMessage", "Book removed from wishlist.");
        return buildBooksRedirect(title, author, category, page, size);
    }

    private String buildBooksRedirect(String title, String author, String category, int page, int size) {
        var builder = org.springframework.web.util.UriComponentsBuilder.fromPath("/books")
                .queryParam("page", Math.max(page, 0))
                .queryParam("size", Math.max(1, Math.min(size, 50)));

        if (title != null && !title.isBlank()) {
            builder.queryParam("title", title);
        }
        if (author != null && !author.isBlank()) {
            builder.queryParam("author", author);
        }
        if (category != null && !category.isBlank()) {
            builder.queryParam("category", category);
        }

        return "redirect:" + builder.toUriString();
    }
}
