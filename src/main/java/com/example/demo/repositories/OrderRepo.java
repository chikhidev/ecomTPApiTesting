package com.example.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entities.Order;

/**
 *
 * @author aberrahimchikhi
 */
@Repository
public interface OrderRepo extends JpaRepository<Order, Long> {}
