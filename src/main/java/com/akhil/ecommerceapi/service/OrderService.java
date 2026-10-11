package com.akhil.ecommerceapi.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.akhil.ecommerceapi.entity.*;
import com.akhil.ecommerceapi.event.OrderPlacedEvent;
import com.akhil.ecommerceapi.exception.InsufficientStockException;
import com.akhil.ecommerceapi.repository.CartRepository;
import com.akhil.ecommerceapi.repository.OrderRepository;

import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
	
	private final OrderRepository orderRepository;
	private final CartRepository cartRepository;
	private final CartService cartService;
	private final ProductService productService;
	private final ApplicationEventPublisher eventPublisher;
	private final UserService userService;
	
	@Autowired
    public OrderService(OrderRepository orderRepository, CartService cartService, ProductService productService, CartRepository cartRepository, ApplicationEventPublisher eventPublisher, UserService userService) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.productService = productService;
        this.cartRepository = cartRepository;
        this.userService = userService;
        this.eventPublisher = eventPublisher;
    }

	@Transactional
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

     // Step 2: build the Order and its OrderItems, deduct stock as we go
        Order order = new Order();
        order.setUser(cart.getUser());
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPriceAtPurchase(product.getPrice());
            orderItems.add(orderItem);

            totalAmount = totalAmount.add(
                product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()))
            );

            product.setStock(product.getStock() - cartItem.getQuantity());
        }

        order.setItems(orderItems);
        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);
        
        // Add kafka event

        User user = userService.getUserById(userId);
        
        OrderPlacedEvent event = new OrderPlacedEvent(
    	    savedOrder.getId(),
    	    user.getEmail(),
    	    savedOrder.getTotalAmount()
    	);
        eventPublisher.publishEvent(event);

        // Step 3: clear the cart now that checkout succeeded
        cart.getItems().clear();
        cartRepository.save(cart);

        return savedOrder;
    }
	
	public List<Order> getOrdersByUserId(Long userId) {
	    return orderRepository.findByUserId(userId);
	}

}
