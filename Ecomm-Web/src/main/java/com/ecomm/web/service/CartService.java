package com.ecomm.web.service;

import com.ecomm.web.dto.CartItemView;
import com.ecomm.web.model.Product;
import com.ecomm.web.repository.ProductRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class CartService {

    private static final String CART_SESSION_KEY = "cart";
    private final ProductRepository productRepository;

    public CartService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void addItem(Long productId, HttpSession session) {
        productRepository.findById(productId).orElseThrow();
        Map<Long, Integer> cart = getOrCreateCart(session);
        cart.put(productId, cart.getOrDefault(productId, 0) + 1);
        session.setAttribute(CART_SESSION_KEY, cart);
    }

    public void removeItem(Long productId, HttpSession session) {
        Map<Long, Integer> cart = getOrCreateCart(session);
        cart.remove(productId);
        session.setAttribute(CART_SESSION_KEY, cart);
    }

    public void clear(HttpSession session) {
        session.setAttribute(CART_SESSION_KEY, new LinkedHashMap<Long, Integer>());
    }

    public int getCount(HttpSession session) {
        return getOrCreateCart(session).values().stream().mapToInt(Integer::intValue).sum();
    }

    public List<CartItemView> getItems(HttpSession session) {
        Map<Long, Integer> cart = getOrCreateCart(session);
        return cart.entrySet().stream()
                .map(entry -> productRepository.findById(entry.getKey())
                        .map(product -> toItem(product, entry.getValue()))
                        .orElse(null))
                .filter(item -> item != null)
                .toList();
    }

    public BigDecimal getTotal(HttpSession session) {
        return getItems(session).stream()
                .map(CartItemView::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @SuppressWarnings("unchecked")
    public Map<Long, Integer> getOrCreateCart(HttpSession session) {
        Object cart = session.getAttribute(CART_SESSION_KEY);
        if (cart instanceof Map<?, ?>) {
            return (Map<Long, Integer>) cart;
        }
        Map<Long, Integer> emptyCart = new LinkedHashMap<>();
        session.setAttribute(CART_SESSION_KEY, emptyCart);
        return emptyCart;
    }

    private CartItemView toItem(Product product, Integer quantity) {
        return new CartItemView(
                product,
                quantity,
                product.getPrice().multiply(BigDecimal.valueOf(quantity))
        );
    }
}
