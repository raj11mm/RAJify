package com.ecomm.web.service;

import com.ecomm.web.dto.CartItemView;
import com.ecomm.web.model.AppUser;
import com.ecomm.web.model.CustomerOrder;
import com.ecomm.web.model.OrderItem;
import com.ecomm.web.repository.CustomerOrderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final CustomerOrderRepository customerOrderRepository;

    public OrderService(CustomerOrderRepository customerOrderRepository) {
        this.customerOrderRepository = customerOrderRepository;
    }

    public CustomerOrder placeOrder(AppUser user, String shippingAddress, List<CartItemView> items) {
        CustomerOrder order = new CustomerOrder();
        order.setUser(user);
        order.setShippingAddress(shippingAddress.trim());
        order.setCreatedAt(LocalDateTime.now());
        order.setTotalAmount(items.stream().map(CartItemView::lineTotal).reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add));

        for (CartItemView cartItem : items) {
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(cartItem.product());
            item.setQuantity(cartItem.quantity());
            item.setUnitPrice(cartItem.product().getPrice());
            order.getItems().add(item);
        }
        return customerOrderRepository.save(order);
    }

    public CustomerOrder getOrder(Long id) {
        return customerOrderRepository.findById(id).orElseThrow();
    }
}
