package com.example.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entities.Category;

/**
 *
 * @author aberrahimchikhi
 */
@Repository
public interface CategoryRepo extends JpaRepository<Category, Long> {
}
