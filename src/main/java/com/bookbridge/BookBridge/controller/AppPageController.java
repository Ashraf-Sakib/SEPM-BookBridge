package com.bookbridge.BookBridge.controller;

import com.bookbridge.BookBridge.dto.response.PageResponse;
import com.bookbridge.BookBridge.dto.response.TransactionResponse;
import com.bookbridge.BookBridge.dto.response.UserResponse;
import com.bookbridge.BookBridge.entity.Role;
import com.bookbridge.BookBridge.service.TransactionService;
import com.bookbridge.BookBridge.service.UserService;
import com.bookbridge.BookBridge.service.WishlistService;
import java.security.Principal;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AppPageController {

    private static final int ACCOUNT_DELETION_GRACE_DAYS = 14;

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

    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminUsersPage(
            @RequestParam(defaultValue = "ALL") String role,
            @RequestParam(defaultValue = "ALL") String status,
            Model model) {

        List<UserResponse> users = userService.getUsersForAdmin(role, status);
        model.addAttribute("users", users);
        model.addAttribute("selectedRole", role.toUpperCase());
        model.addAttribute("selectedStatus", status.toUpperCase());
        model.addAttribute("deletionGraceDays", ACCOUNT_DELETION_GRACE_DAYS);
        return "admin/users";
    }

    @PostMapping("/admin/users/{userId}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateUserRoles(@PathVariable Integer userId,
                                  @RequestParam(name = "roles", required = false) List<String> roles,
                                  RedirectAttributes redirectAttributes) {
        try {
            Set<Role.RoleName> requestedRoles = EnumSet.noneOf(Role.RoleName.class);
            if (roles != null) {
                for (String role : roles) {
                    requestedRoles.add(Role.RoleName.valueOf(role));
                }
            }

            userService.assignMarketplaceRoles(userId, requestedRoles);
            redirectAttributes.addFlashAttribute("successMessage", "User roles updated successfully.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/admin/users/{userId}/disable")
    @PreAuthorize("hasRole('ADMIN')")
    public String disableUser(@PathVariable Integer userId,
                              RedirectAttributes redirectAttributes) {
        try {
            userService.disableUser(userId);
            redirectAttributes.addFlashAttribute("successMessage", "User disabled successfully.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/admin/users/{userId}/enable")
    @PreAuthorize("hasRole('ADMIN')")
    public String enableUser(@PathVariable Integer userId,
                             RedirectAttributes redirectAttributes) {
        try {
            userService.enableUser(userId);
            redirectAttributes.addFlashAttribute("successMessage", "User enabled and unscheduled for deletion.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/admin/users/{userId}/schedule-delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String scheduleDelete(@PathVariable Integer userId,
                                 RedirectAttributes redirectAttributes) {
        try {
            userService.scheduleDeletion(userId, ACCOUNT_DELETION_GRACE_DAYS);
            redirectAttributes.addFlashAttribute("successMessage",
                    "User disabled and scheduled for permanent deletion in " + ACCOUNT_DELETION_GRACE_DAYS + " days.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/admin/users/{userId}/cancel-delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String cancelScheduledDelete(@PathVariable Integer userId,
                                        RedirectAttributes redirectAttributes) {
        try {
            userService.cancelScheduledDeletion(userId);
            redirectAttributes.addFlashAttribute("successMessage", "Scheduled deletion canceled.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/users";
    }
}

