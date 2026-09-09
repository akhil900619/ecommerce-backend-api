package com.akhil.ecommerceapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.akhil.ecommerceapi.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long>{

}
