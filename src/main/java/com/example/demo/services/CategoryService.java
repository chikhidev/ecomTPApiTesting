package com.example.demo.services;

import java.rmi.UnexpectedException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.entities.Category;
import com.example.demo.repositories.CategoryRepo;

/**
 * Service for managing Category operations
 * @author aberrahimchikhi
 */
@Service
public class CategoryService {
    final private CategoryRepo categoryRepo;

    public CategoryService(CategoryRepo categoryRepo) {
        this.categoryRepo = categoryRepo;
    }

    public List<Category> getAll() {
        return this.categoryRepo.findAll();
    }

    public Category findById(Long id) throws NotFoundException {
        Optional<Category> found = this.categoryRepo.findById(id);

        if (found.isPresent()) return found.get();

        throw new NotFoundException();
    }

    public Category create(Category category) {
        return this.categoryRepo.save(category);
    }

    public Category update(Long id, Category category) throws NotFoundException {
        Optional<Category> found = this.categoryRepo.findById(id);

        if (found.isPresent()) {
            BeanUtils.copyProperties(category, found.get(), "id", "createdAt");

            return this.categoryRepo.save(found.get());
        }

        throw new NotFoundException();
    }

    public void delete(Long id) throws UnexpectedException, NotFoundException {
        Optional<Category> found = this.categoryRepo.findById(id);

        if (found.isEmpty()) {
            throw new NotFoundException();
        }
    
        try {
            this.categoryRepo.delete(found.get());
        } catch (Exception e) {
            throw new UnexpectedException("Could not delete category with id: " + id);
        }
    }

}
