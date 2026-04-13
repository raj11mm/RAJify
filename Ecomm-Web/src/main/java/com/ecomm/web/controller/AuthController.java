package com.ecomm.web.controller;

import com.ecomm.web.service.AuthService;
import com.ecomm.web.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final AuthService authService;
    private final CartService cartService;

    public AuthController(AuthService authService, CartService cartService) {
        this.authService = authService;
        this.cartService = cartService;
    }

    @GetMapping("/auth")
    public String authPage(HttpSession session, Model model) {
        model.addAttribute("cartCount", cartService.getCount(session));
        model.addAttribute("currentUser", authService.getCurrentUser(session));
        return "auth";
    }

    @PostMapping("/auth/register")
    public String register(
            @RequestParam String fullName,
            @RequestParam String email,
            @RequestParam String password,
            RedirectAttributes redirectAttributes
    ) {
        if (fullName.isBlank() || email.isBlank() || password.isBlank()) {
            redirectAttributes.addFlashAttribute("authError", "Please fill all registration fields.");
            return "redirect:/auth";
        }
        boolean success = authService.register(fullName, email, password);
        if (!success) {
            redirectAttributes.addFlashAttribute("authError", "Email already exists. Please login.");
            return "redirect:/auth";
        }
        redirectAttributes.addFlashAttribute("authSuccess", "Registration successful. Please login.");
        return "redirect:/auth";
    }

    @PostMapping("/auth/login")
    public String login(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        if (authService.login(email, password, session)) {
            redirectAttributes.addFlashAttribute("successMessage", "Welcome back!");
            return "redirect:/";
        }
        redirectAttributes.addFlashAttribute("authError", "Invalid credentials.");
        return "redirect:/auth";
    }

    @PostMapping("/auth/logout")
    public String logout(HttpSession session) {
        authService.logout(session);
        return "redirect:/";
    }
}
