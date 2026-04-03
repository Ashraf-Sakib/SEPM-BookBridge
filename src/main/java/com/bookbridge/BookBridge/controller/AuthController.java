package com.bookbridge.BookBridge.controller;

import com.bookbridge.BookBridge.dto.request.RegisterRequest;
import com.bookbridge.BookBridge.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller  // NOT @RestController — returns Thymeleaf view names
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // ── Home page ──────────────────────────────────────────
    @GetMapping("/")
    public String home() {
        return "index";
    }

    // ── Show login page ────────────────────────────────────
    @GetMapping("/login")
    public String showLogin(
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String logout,
            Model model) {

        if (error != null) {
            model.addAttribute("errorMessage",
                    "Incorrect username or password. Please try again.");
        }
        if (logout != null) {
            model.addAttribute("logoutMessage",
                    "You have been logged out successfully.");
        }
        return "auth/login"; // → templates/auth/login.html
    }

    // ── Show register page ─────────────────────────────────
    @GetMapping("/register")
    public String showRegister(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "auth/register"; // → templates/auth/register.html
    }

    // ── Handle register form submit ────────────────────────
    @PostMapping("/register")
    public String handleRegister(
            @Valid @ModelAttribute("registerRequest") RegisterRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        // If @Valid found errors (blank fields, bad email etc) — show form again
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        try {
            authService.register(request);
            // Success — redirect to login with success message
            redirectAttributes.addFlashAttribute("successMessage",
                    "Account created! Please log in.");
            return "redirect:/login";

        } catch (IllegalArgumentException e) {
            // Username or email already taken
            model.addAttribute("errorMessage", e.getMessage());
            return "auth/register";
        }
    }

    // ── Dashboard after login ──────────────────────────────
    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }
}