package com.akhil.ecommerceapi.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.akhil.ecommerceapi.entity.Cart;
import com.akhil.ecommerceapi.entity.CartItem;
import com.akhil.ecommerceapi.entity.Product;
import com.akhil.ecommerceapi.entity.User;
import com.akhil.ecommerceapi.exception.CartItemNotFoundException;
import com.akhil.ecommerceapi.repository.CartRepository;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final UserService userService;
    private final ProductService productService;

    @Autowired
    public CartService(CartRepository cartRepository, UserService userService, ProductService productService) {
        this.cartRepository = cartRepository;
        this.userService = userService;
        this.productService = productService;
    }

    public Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> createCartForUser(userId));
    }

    private Cart createCartForUser(Long userId) {
        User user = userService.getUserById(userId);
        Cart newCart = new Cart();
        newCart.setUser(user);
        return cartRepository.save(newCart);
    }
    
    public Cart addItemToCart(Long userId, Long productId, Integer quantity) {
    	Cart cart = getOrCreateCart(userId);
    	Product product = productService.getProductById(productId);
    	
    	Optional<CartItem> existingItem = cart.getItems().stream()
    			.filter(item -> item.getProduct().getId().equals(productId))
    			.findFirst();
    	
    	if (existingItem.isPresent()) {
    		CartItem item = existingItem.get();
    		item.setQuantity(item.getQuantity() + quantity);
    	} else {
			CartItem newItem = new CartItem();
			newItem.setCart(cart);
			newItem.setProduct(product);
			newItem.setQuantity(quantity);
			cart.getItems().add(newItem);
		}
    	
    	return cartRepository.save(cart);
    }
    
    public Cart updateItemQuantity(Long userId, Long cartItemId, Integer newQuantity) {
    	Cart cart = getOrCreateCart(userId);
    	
    	CartItem item = cart.getItems().stream()
    			.filter(i -> i.getId().equals(cartItemId))
    			.findFirst()
    			.orElseThrow(() -> new CartItemNotFoundException("Cart item not found with id: " + cartItemId));
    	
    	item.setQuantity(newQuantity);
    	return cartRepository.save(cart);
    }
    
    public Cart removeItemFromCart(Long userId, Long cartItemId) {
    	Cart cart = getOrCreateCart(userId);
    	
    	boolean removed = cart.getItems().removeIf(item -> item.getId().equals(cartItemId));
    	
    	if(!removed) {
    		throw new CartItemNotFoundException("Cart item not found with id: " + cartItemId);	
    	}
    	
    	return cartRepository.save(cart);
    }

}