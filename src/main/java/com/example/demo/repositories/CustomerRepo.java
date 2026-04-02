package com.example.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entities.Customer;
import java.util.List;


/**
 *
 * @author aberrahimchikhi
 */
@Repository
public interface CustomerRepo extends JpaRepository<Customer, Long> {
    List<Customer> findByEmail(String email);
}
