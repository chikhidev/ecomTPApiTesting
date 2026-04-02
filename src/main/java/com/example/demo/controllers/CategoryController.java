package com.example.demo.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entities.Category;
import com.example.demo.services.CategoryService;

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
 * REST Controller for Category CRUD operations
 * @author aberrahimchikhi
 */
@RestController
@RequestMapping("categories")
public class CategoryController {

    final private CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping()
    public List<Category> getAllCategories() {
        return this.categoryService.getAll();
    }

    @GetMapping("{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) throws NotFoundException {
        Category category = this.categoryService.findById(id);
        return ResponseEntity.status(200).body(category);
    }

    @PostMapping()
    public ResponseEntity<Category> createCategory(@RequestBody Category category)
        throws InvalidParameterException    
    {
        if (category.getId() != null) throw new InvalidParameterException("ID must not be provided on creation");
        if (category.getName() == null || category.getName().length() == 0) throw new InvalidParameterException("Invalid category name");

        Category created = this.categoryService.create(category);

        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody Category payload)
            throws NotFoundException, InvalidParameterException
    {
        if (payload.getName() == null || payload.getName().length() == 0) throw new InvalidParameterException("Invalid category name");

        Category updated = this.categoryService.update(id, payload);
        return ResponseEntity.status(200).body(updated);
    }

    @PatchMapping("{id}")
    public ResponseEntity<Category> updatePartialCategory(@PathVariable Long id, @RequestBody Category payload)
            throws NotFoundException
    {
        Category updated = this.categoryService.update(id, payload);
        return ResponseEntity.status(200).body(updated);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Category> deleteCategory(@PathVariable Long id)
            throws NotFoundException, UnexpectedException
    {

        this.categoryService.delete(id);
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
