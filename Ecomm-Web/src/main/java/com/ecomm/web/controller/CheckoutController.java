package com.ecomm.web.controller;

import com.ecomm.web.dto.CartItemView;
import com.ecomm.web.model.AppUser;
import com.ecomm.web.model.CustomerOrder;
import com.ecomm.web.service.AuthService;
import com.ecomm.web.service.CartService;
import com.ecomm.web.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class CheckoutController {

    private final AuthService authService;
    private final CartService cartService;
    private final OrderService orderService;

    public CheckoutController(AuthService authService, CartService cartService, OrderService orderService) {
        this.authService = authService;
        this.cartService = cartService;
        this.orderService = orderService;
    }

    @GetMapping("/checkout")
    public String checkout(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        AppUser currentUser = authService.getCurrentUser(session);
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("authError", "Please login to continue checkout.");
            return "redirect:/auth";
        }

        List<CartItemView> cartItems = cartService.getItems(session);
        if (cartItems.isEmpty()) {
            redirectAttributes.addFlashAttribute("successMessage", "Your cart is empty. Add some products first.");
            return "redirect:/";
        }

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("cartCount", cartService.getCount(session));
        model.addAttribute("cartTotal", cartService.getTotal(session));
        return "checkout";
    }

    @PostMapping("/checkout/place")
    public String placeOrder(
            @RequestParam String shippingAddress,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        AppUser currentUser = authService.getCurrentUser(session);
        if (currentUser == null) {
            return "redirect:/auth";
        }
        List<CartItemView> items = cartService.getItems(session);
        if (items.isEmpty()) {
            return "redirect:/";
        }
        if (shippingAddress.isBlank()) {
            redirectAttributes.addFlashAttribute("checkoutError", "Shipping address is required.");
            return "redirect:/checkout";
        }

        CustomerOrder order = orderService.placeOrder(currentUser, shippingAddress, items);
        cartService.clear(session);
        return "redirect:/order/confirmation/" + order.getId();
    }

    @GetMapping("/order/confirmation/{id}")
    public String confirmation(@PathVariable Long id, HttpSession session, Model model) {
        CustomerOrder order = orderService.getOrder(id);
        model.addAttribute("order", order);
        model.addAttribute("cartCount", cartService.getCount(session));
        model.addAttribute("currentUser", authService.getCurrentUser(session));
        return "order-confirmation";
    }
}
