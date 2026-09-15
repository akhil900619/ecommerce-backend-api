package com.akhil.ecommerceapi.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.akhil.ecommerceapi.entity.Cart;

public interface CartRepository extends JpaRepository<Cart, Long>{

	Optional<Cart> findByUserId(Long userId);
	
}
