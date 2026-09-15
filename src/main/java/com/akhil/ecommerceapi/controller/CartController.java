package com.akhil.ecommerceapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.akhil.ecommerceapi.entity.Cart;
import com.akhil.ecommerceapi.service.CartService;

@RestController
@RequestMapping("/api/cart")
public class CartController {
	
	private final CartService cartService;
	
	@Autowired
	public CartController(CartService cartService) {
		this.cartService = cartService;
	}
	
	@GetMapping("/{userId}")
	public ResponseEntity<Cart> getCart(@PathVariable Long userId) {
		Cart cart = cartService.getOrCreateCart(userId);
		return ResponseEntity.ok(cart);
	}
	
	@PostMapping("/{userId}/items")
    public ResponseEntity<Cart> addItem(
            @PathVariable Long userId,
            @RequestParam Long productId,
            @RequestParam Integer quantity) {
        Cart updatedCart = cartService.addItemToCart(userId, productId, quantity);
        return ResponseEntity.ok(updatedCart);
    }

    @PutMapping("/{userId}/items/{itemId}")
    public ResponseEntity<Cart> updateItem(
            @PathVariable Long userId,
            @PathVariable Long itemId,
            @RequestParam Integer quantity) {
        Cart updatedCart = cartService.updateItemQuantity(userId, itemId, quantity);
        return ResponseEntity.ok(updatedCart);
    }

    @DeleteMapping("/{userId}/items/{itemId}")
    public ResponseEntity<Cart> removeItem(
            @PathVariable Long userId,
            @PathVariable Long itemId) {
        Cart updatedCart = cartService.removeItemFromCart(userId, itemId);
        return ResponseEntity.ok(updatedCart);
    }
	

}
