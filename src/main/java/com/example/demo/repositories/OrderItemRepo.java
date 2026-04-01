package com.example.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entities.OrderItem;

/**
 *
 * @author aberrahimchikhi
 */
@Repository
public interface OrderItemRepo extends JpaRepository<OrderItem, Long> {}
