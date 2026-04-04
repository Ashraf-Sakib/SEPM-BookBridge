package com.bookbridge.BookBridge.controller;

import com.bookbridge.BookBridge.dto.request.BookCreateRequest;
import com.bookbridge.BookBridge.dto.response.UserResponse;
import com.bookbridge.BookBridge.entity.Book;
import com.bookbridge.BookBridge.service.BookService;
import com.bookbridge.BookBridge.service.CategoryService;
import com.bookbridge.BookBridge.service.FileStorageService;
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
    private final CategoryService categoryService;
    private final UserService userService;
    private final FileStorageService fileStorageService;

    @GetMapping("/seller/books/new")
    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    public String showSellBookForm(Model model) {
        if (!model.containsAttribute("bookCreateRequest")) {
            BookCreateRequest request = new BookCreateRequest();
            request.setAvailable(true);
            model.addAttribute("bookCreateRequest", request);
        }
        model.addAttribute("categories", categoryService.getAllCategories());
        return "books/sell";
    }

    @PostMapping("/seller/books/new")
    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    public String submitSellBookForm(
            @Valid @ModelAttribute("bookCreateRequest") BookCreateRequest request,
            BindingResult result,
            Principal principal,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
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
        book.setDiscountPercentage(request.getDiscountPercentage());
        book.setAvailable(request.getAvailable() == null ? true : request.getAvailable());
        if (request.getCategorySlug() != null && !request.getCategorySlug().isBlank()) {
            var categoryOptional = categoryService.findBySlug(request.getCategorySlug());
            if (categoryOptional.isEmpty()) {
                result.rejectValue("categorySlug", "category.invalid", "Selected category is invalid.");
                model.addAttribute("categories", categoryService.getAllCategories());
                return "books/sell";
            }
            book.setCategory(categoryOptional.get());
        }

        if (request.getImage() != null && !request.getImage().isEmpty()) {
            if (!fileStorageService.isSupportedImage(request.getImage())) {
                result.rejectValue("image", "image.invalid", "Please upload a JPG, PNG, WEBP, or GIF image.");
                model.addAttribute("categories", categoryService.getAllCategories());
                return "books/sell";
            }
            book.setImageUrl(fileStorageService.store(request.getImage()));
        }

        bookService.createBook(book, currentUser.getId());
        redirectAttributes.addFlashAttribute("successMessage", "Book listed successfully.");
        return "redirect:/books";
    }

    @GetMapping("/seller/books/{bookId}/edit")
    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    public String showEditBookForm(@org.springframework.web.bind.annotation.PathVariable Integer bookId,
                                   Principal principal,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login";
        }

        UserResponse currentUser = userService.getUserByUsername(principal.getName());
        Book book = bookService.getBookEntityById(bookId);
        if (book.getAddedBy() == null || !book.getAddedBy().getId().equals(currentUser.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "You can only edit books you added.");
            return "redirect:/books";
        }

        if (!model.containsAttribute("bookCreateRequest")) {
            BookCreateRequest request = new BookCreateRequest();
            request.setTitle(book.getTitle());
            request.setAuthor(book.getAuthor());
            request.setPrice(book.getPrice());
            request.setDiscountPercentage(book.getDiscountPercentage());
            request.setAvailable(book.getAvailable());
            request.setDescription(book.getDescription());
            request.setCategorySlug(book.getCategory() != null ? book.getCategory().getSlug() : null);
            model.addAttribute("bookCreateRequest", request);
        }

        model.addAttribute("bookId", bookId);
        model.addAttribute("existingImageUrl", book.getImageUrl());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "books/edit";
    }

    @PostMapping("/seller/books/{bookId}/edit")
    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    public String submitEditBookForm(@org.springframework.web.bind.annotation.PathVariable Integer bookId,
                                     @Valid @ModelAttribute("bookCreateRequest") BookCreateRequest request,
                                     BindingResult result,
                                     Principal principal,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {

        if (principal == null) {
            return "redirect:/login";
        }

        UserResponse currentUser = userService.getUserByUsername(principal.getName());
        Book existingBook = bookService.getBookEntityById(bookId);
        if (existingBook.getAddedBy() == null || !existingBook.getAddedBy().getId().equals(currentUser.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "You can only edit books you added.");
            return "redirect:/books";
        }

        if (result.hasErrors()) {
            model.addAttribute("bookId", bookId);
            model.addAttribute("existingImageUrl", existingBook.getImageUrl());
            model.addAttribute("categories", categoryService.getAllCategories());
            return "books/edit";
        }

        Book bookDetails = new Book();
        bookDetails.setTitle(request.getTitle().trim());
        bookDetails.setAuthor(request.getAuthor().trim());
        bookDetails.setDescription(request.getDescription());
        bookDetails.setPrice(request.getPrice());
        bookDetails.setDiscountPercentage(request.getDiscountPercentage());
        bookDetails.setAvailable(request.getAvailable() == null ? true : request.getAvailable());
        if (request.getCategorySlug() != null && !request.getCategorySlug().isBlank()) {
            var categoryOptional = categoryService.findBySlug(request.getCategorySlug());
            if (categoryOptional.isEmpty()) {
                result.rejectValue("categorySlug", "category.invalid", "Selected category is invalid.");
                model.addAttribute("bookId", bookId);
                model.addAttribute("existingImageUrl", existingBook.getImageUrl());
                model.addAttribute("categories", categoryService.getAllCategories());
                return "books/edit";
            }
            bookDetails.setCategory(categoryOptional.get());
        } else {
            bookDetails.setCategory(existingBook.getCategory());
        }

        if (request.getImage() != null && !request.getImage().isEmpty()) {
            if (!fileStorageService.isSupportedImage(request.getImage())) {
                result.rejectValue("image", "image.invalid", "Please upload a JPG, PNG, WEBP, or GIF image.");
                model.addAttribute("bookId", bookId);
                model.addAttribute("existingImageUrl", existingBook.getImageUrl());
                model.addAttribute("categories", categoryService.getAllCategories());
                return "books/edit";
            }
            bookDetails.setImageUrl(fileStorageService.store(request.getImage()));
        } else {
            bookDetails.setImageUrl(existingBook.getImageUrl());
        }

        bookService.updateBookForOwner(bookId, currentUser.getId(), bookDetails);
        redirectAttributes.addFlashAttribute("successMessage", "Book details updated.");
        return "redirect:/books";
    }
}