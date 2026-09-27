package com.akhil.ecommerceapi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.akhil.ecommerceapi.entity.*;
import com.akhil.ecommerceapi.exception.InsufficientStockException;
import com.akhil.ecommerceapi.repository.CartRepository;
import com.akhil.ecommerceapi.repository.OrderRepository;

import org.mockito.Mock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
	
	@Mock
    private CartService cartService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartRepository cartRepository;

    @InjectMocks
    private OrderService orderService;
    
    @Test
    void checkout_throwsException_whenCartIsEmpty() {
        // Arrange: set up the fake data and fake behavior
        Long userId = 1L;
        Cart emptyCart = new Cart();
        emptyCart.setItems(new ArrayList<>());
        when(cartService.getOrCreateCart(userId)).thenReturn(emptyCart);

        // Act + Assert: call the real method, verify it throws
        assertThrows(IllegalStateException.class, () -> orderService.checkout(userId));
    }
    
    @Test
    void checkout_throwsException_whenStockIsInsufficient() {
    	// Arrange
    	Long userId = 1L;
    	
    	Product product = new Product();
    	product.setId(1L);
    	product.setPrice(new BigDecimal("100.00"));
    	product.setStock(2);
    	
    	CartItem cartItem = new CartItem();
    	cartItem.setProduct(product);
    	cartItem.setQuantity(5);
    	
    	Cart cart = new Cart();
    	cart.setItems(new ArrayList<>(List.of(cartItem)));
    	
    	when(cartService.getOrCreateCart(userId)).thenReturn(cart);
    	
    	assertThrows(InsufficientStockException.class, () -> orderService.checkout(userId));
    	
    }
    
    @Test
    void checkout_succeeds_andCalculatesCorrectTotal_whenStockIsSufficient() {
        // Arrange
        Long userId = 1L;

        Product product = new Product();
        product.setId(1L);
        product.setPrice(new BigDecimal("100.00"));
        product.setStock(10);

        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(3);

        Cart cart = new Cart();
        cart.setItems(new ArrayList<>(List.of(cartItem)));

        when(cartService.getOrCreateCart(userId)).thenReturn(cart);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Order result = orderService.checkout(userId);

        // Assert
        assertEquals(new BigDecimal("300.00"), result.getTotalAmount());
        assertEquals(7, product.getStock()); // 10 - 3 = 7
        assertEquals(new BigDecimal("100.00"), result.getItems().get(0).getPriceAtPurchase());
    }

}
