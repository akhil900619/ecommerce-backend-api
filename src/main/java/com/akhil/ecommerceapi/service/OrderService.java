package com.akhil.ecommerceapi.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.akhil.ecommerceapi.entity.*;
import com.akhil.ecommerceapi.exception.InsufficientStockException;
import com.akhil.ecommerceapi.repository.OrderRepository;

public class OrderService {
	
	private final OrderRepository orderRepository;
	private final CartService cartService;
	private final ProductService productService;
	
	@Autowired
    public OrderService(OrderRepository orderRepository, CartService cartService, ProductService productService) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.productService = productService;
    }

    public Order checkout(Long userId) {
        Cart cart = cartService.getOrCreateCart(userId);

        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot checkout an empty cart");
        }

        // Step 1: validate stock for EVERY item before changing anything
        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            if (product.getStock() < cartItem.getQuantity()) {
                throw new InsufficientStockException(
                    "Insufficient stock for product: " + product.getName() +
                    ". Available: " + product.getStock() + ", Requested: " + cartItem.getQuantity()
                );
            }
        }

        // more steps coming next...
        return null; // placeholder for now
    }

}
