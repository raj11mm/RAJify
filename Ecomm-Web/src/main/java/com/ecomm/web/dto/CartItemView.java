package com.ecomm.web.dto;

import com.ecomm.web.model.Product;

import java.math.BigDecimal;

public record CartItemView(Product product, int quantity, BigDecimal lineTotal) {
}
