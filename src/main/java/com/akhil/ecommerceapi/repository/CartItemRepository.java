package com.akhil.ecommerceapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.akhil.ecommerceapi.entity.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long>{

}
