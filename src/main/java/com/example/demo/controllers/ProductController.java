package com.example.demo.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entities.Product;
import com.example.demo.services.ProductService;

import java.rmi.UnexpectedException;
import java.util.List;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("products")
public class ProductController {

    final private ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping()
    public List<Product> getAllProducts() {
        return this.productService.getAll();
    }

    @PostMapping()
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product created = this.productService.create(product);

        return ResponseEntity.ok(created);
    }

    @PutMapping("{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product payload)
            throws NotFoundException
    {
        // should be validated leter here!!!!!!

        Product updated = this.productService.update(id, payload);
        return ResponseEntity.status(201).body(updated);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Product> deleteProduct(@PathVariable Long id)
            throws NotFoundException, UnexpectedException
    {
        // should be validated leter here!!!!!!

        this.productService.delete(id);
        return ResponseEntity.status(204).build();
    }

    // ERROR HANDLERS

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleException(NotFoundException ex) {
        return ResponseEntity.status(404).body("Could not find this product");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleOtherExceptions(Exception ex) {
        return ResponseEntity.status(500).body("Something went wrong" + ex.getMessage());
    }

}
