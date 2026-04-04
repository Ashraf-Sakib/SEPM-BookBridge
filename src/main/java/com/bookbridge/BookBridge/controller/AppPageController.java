package com.bookbridge.BookBridge.controller;

import com.bookbridge.BookBridge.dto.response.PageResponse;
import com.bookbridge.BookBridge.dto.response.TransactionResponse;
import com.bookbridge.BookBridge.dto.response.UserResponse;
import com.bookbridge.BookBridge.service.TransactionService;
import com.bookbridge.BookBridge.service.UserService;
import com.bookbridge.BookBridge.service.WishlistService;
import java.security.Principal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AppPageController {

    private final TransactionService transactionService;
    private final UserService userService;
    private final WishlistService wishlistService;

    public AppPageController(TransactionService transactionService, UserService userService, WishlistService wishlistService) {
        this.transactionService = transactionService;
        this.userService = userService;
        this.wishlistService = wishlistService;
    }

    @GetMapping("/orders")
    public String ordersPage(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }

        UserResponse currentUser = userService.getUserByUsername(principal.getName());
        PageResponse<TransactionResponse> purchaseOrders = transactionService.getBuyerTransactions(currentUser.getId(), 0, 50);
        PageResponse<TransactionResponse> salesOrders = transactionService.getSellerTransactions(currentUser.getId(), 0, 50);

        model.addAttribute("purchaseOrders", purchaseOrders.getContent());
        model.addAttribute("salesOrders", salesOrders.getContent());
        model.addAttribute("currentUsername", currentUser.getUsername());
        return "orders";
    }

    @GetMapping("/wishlist")
    public String wishlistPage(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }

        UserResponse currentUser = userService.getUserByUsername(principal.getName());
        model.addAttribute("wishlistItems", wishlistService.getWishlistByUserId(currentUser.getId()));
        return "wishlist";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminPage() {
        return "admin";
    }
}