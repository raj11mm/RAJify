package com.ecomm.web.controller;

import com.ecomm.web.model.AppUser;
import com.ecomm.web.model.Product;
import com.ecomm.web.repository.ProductRepository;
import com.ecomm.web.service.AuthService;
import com.ecomm.web.service.CartService;
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
public class HomeController {

    private final ProductRepository productRepository;
    private final CartService cartService;
    private final AuthService authService;

    public HomeController(ProductRepository productRepository, CartService cartService, AuthService authService) {
        this.productRepository = productRepository;
        this.cartService = cartService;
        this.authService = authService;
    }

    @GetMapping("/")
    public String home(
            @RequestParam(defaultValue = "all") String category,
            @RequestParam(defaultValue = "") String q,
            Model model,
            HttpSession session
    ) {
        String normalizedQuery = q == null ? "" : q.trim();
        String normalizedCategory = category == null ? "all" : category.trim();

        List<Product> products;
        if (!normalizedQuery.isBlank() && !"all".equalsIgnoreCase(normalizedCategory)) {
            products = productRepository
                    .findByCategoryIgnoreCaseAndNameContainingIgnoreCaseOrCategoryIgnoreCaseAndDescriptionContainingIgnoreCaseOrderByIdAsc(
                            normalizedCategory, normalizedQuery, normalizedCategory, normalizedQuery
                    );
        } else if (!normalizedQuery.isBlank()) {
            products = productRepository
                    .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrderByIdAsc(normalizedQuery, normalizedQuery);
        } else if (!"all".equalsIgnoreCase(normalizedCategory)) {
            products = productRepository.findByCategoryIgnoreCaseOrderByIdAsc(normalizedCategory);
        } else {
            products = productRepository.findAll().stream().sorted((a, b) -> a.getId().compareTo(b.getId())).toList();
        }

        model.addAttribute("products", products);
        model.addAttribute("selectedCategory", normalizedCategory.toLowerCase());
        model.addAttribute("query", normalizedQuery);
        model.addAttribute("cartItems", cartService.getItems(session));
        model.addAttribute("cartCount", cartService.getCount(session));
        model.addAttribute("cartTotal", cartService.getTotal(session));
        model.addAttribute("currentUser", authService.getCurrentUser(session));
        return "index";
    }

    @GetMapping("/products/{id}")
    public String productDetail(@PathVariable Long id, Model model, HttpSession session) {
        Product product = productRepository.findById(id).orElseThrow();
        AppUser currentUser = authService.getCurrentUser(session);
        model.addAttribute("product", product);
        model.addAttribute("cartCount", cartService.getCount(session));
        model.addAttribute("currentUser", currentUser);
        return "product-detail";
    }

    @PostMapping("/cart/add/{id}")
    public String addToCart(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        cartService.addItem(id, session);
        redirectAttributes.addFlashAttribute("successMessage", "Item added to cart.");
        return "redirect:/";
    }

    @PostMapping("/cart/remove/{id}")
    public String removeFromCart(@PathVariable Long id, HttpSession session) {
        cartService.removeItem(id, session);
        return "redirect:/";
    }
}
