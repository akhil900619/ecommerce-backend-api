package com.akhil.ecommerceapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.akhil.ecommerceapi.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long>{

}
