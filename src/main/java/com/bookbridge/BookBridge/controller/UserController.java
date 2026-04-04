package com.bookbridge.BookBridge.controller;

import com.bookbridge.BookBridge.dto.response.UserResponse;
import com.bookbridge.BookBridge.dto.response.WishlistItemResponse;
import com.bookbridge.BookBridge.entity.User;
import com.bookbridge.BookBridge.service.UserService;
import com.bookbridge.BookBridge.service.WishlistService;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final WishlistService wishlistService;

    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN','BUYER','SELLER')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Integer userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @GetMapping("/username/{username}")
    @PreAuthorize("hasAnyRole('ADMIN','BUYER','SELLER')")
    public ResponseEntity<UserResponse> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN','BUYER','SELLER')")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Integer userId,
            @RequestBody User userDetails) {
        return ResponseEntity.ok(userService.updateUser(userId, userDetails));
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me/wishlist")
    @PreAuthorize("hasAnyRole('ADMIN','BUYER','SELLER')")
    public ResponseEntity<List<WishlistItemResponse>> getMyWishlist(Principal principal) {
        Integer userId = userService.getUserByUsername(principal.getName()).getId();
        return ResponseEntity.ok(wishlistService.getWishlistByUserId(userId));
    }

    @PostMapping("/me/wishlist/{bookId}")
    @PreAuthorize("hasAnyRole('ADMIN','BUYER','SELLER')")
    public ResponseEntity<Void> saveBookToWishlist(@PathVariable Integer bookId, Principal principal) {
        Integer userId = userService.getUserByUsername(principal.getName()).getId();
        wishlistService.addBook(userId, bookId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/me/wishlist/{bookId}")
    @PreAuthorize("hasAnyRole('ADMIN','BUYER','SELLER')")
    public ResponseEntity<Void> removeBookFromWishlist(@PathVariable Integer bookId, Principal principal) {
        Integer userId = userService.getUserByUsername(principal.getName()).getId();
        wishlistService.removeBook(userId, bookId);
        return ResponseEntity.noContent().build();
    }
}