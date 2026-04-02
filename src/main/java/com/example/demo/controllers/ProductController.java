package com.example.demo.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entities.Product;
import com.example.demo.services.CategoryService;
import com.example.demo.services.ProductService;
import com.example.demo.entities.Category;

import java.math.BigDecimal;
import java.rmi.UnexpectedException;
import java.security.InvalidParameterException;
import java.util.List;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * REST Controller for Product CRUD operations
 * @author aberrahimchikhi
 */
@RestController
@RequestMapping("products")
public class ProductController {

    final private ProductService productService;
    final private CategoryService categoryService;

    public ProductController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @GetMapping()
    public List<Product> getAllProducts() {
        return this.productService.getAll();
    }

    @GetMapping("{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) throws NotFoundException {
        Product product = this.productService.findById(id);
        return ResponseEntity.status(200).body(product);
    }

    @PostMapping()
    public ResponseEntity<Product> createProduct(@RequestBody Product product)
        throws InvalidParameterException, NotFoundException
    {
        if (product.getId() != null) throw new InvalidParameterException("ID must not be provided on creation");
        if (product.getName() == null || product.getName().length() == 0) throw new InvalidParameterException("Invalid name");
        if (product.getPrice().compareTo(BigDecimal.ZERO) <= 0) throw new InvalidParameterException("Invalid price");
        if (product.getStockQuantity() <= 0) throw new InvalidParameterException("Invalid stock");

        Product created = this.productService.create(product);

        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product payload)
            throws NotFoundException
    {
        if (payload.getName() == null || payload.getName().length() == 0) throw new InvalidParameterException("Invalid name");
        if (payload.getPrice().compareTo(BigDecimal.ZERO) <= 0) throw new InvalidParameterException("Invalid price");
        if (payload.getStockQuantity() <= 0) throw new InvalidParameterException("Invalid stock");

        Product updated = this.productService.update(id, payload);
        return ResponseEntity.status(200).body(updated);
    }

    @PatchMapping("{id}")
    public ResponseEntity<Product> updatePartialProduct(@PathVariable Long id, @RequestBody Product payload)
            throws NotFoundException
    {
        if (payload.getCategory() != null && payload.getCategory().getId() != null) {
            Category dbCategory = this.categoryService.findById(payload.getCategory().getId());
            payload.setCategory(dbCategory);
        }

        Product updated = this.productService.update(id, payload);
        return ResponseEntity.status(200).body(updated);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Product> deleteProduct(@PathVariable Long id)
            throws NotFoundException, UnexpectedException
    {

        this.productService.delete(id);
        return ResponseEntity.status(204).build();
    }

    // ERROR HANDLERS

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleException(NotFoundException ex) {
        return ResponseEntity.status(404).body("NOT FOUND");
    }

    @ExceptionHandler(InvalidParameterException.class)
    public ResponseEntity<String> handleInvalidParam(InvalidParameterException ex) {
        return ResponseEntity.status(400).body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleOtherExceptions(Exception ex) {
        return ResponseEntity.status(500).body("Something went wrong: " + ex.getMessage());
    }

}
