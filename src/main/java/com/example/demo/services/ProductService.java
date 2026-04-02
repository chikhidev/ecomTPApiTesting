package com.example.demo.services;

import java.rmi.UnexpectedException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.entities.Product;
import com.example.demo.entities.Category;
import com.example.demo.repositories.ProductRepo;
import com.example.demo.repositories.CategoryRepo;

@Service
public class ProductService {
    final private ProductRepo productRepo;
    final private CategoryRepo categoryRepo;

    public ProductService(ProductRepo productRepo, CategoryRepo categoryRepo) {
        this.productRepo = productRepo;
        this.categoryRepo = categoryRepo;
    }

    public List<Product> getAll() {
        return this.productRepo.findAll();
    }

    public Product findById(Long id) throws NotFoundException {
        Optional<Product> found = this.productRepo.findById(id);

        if (found.isPresent())
            return found.get();

        throw new NotFoundException();
    }

    public Product create(Product product) throws NotFoundException {
        if (product.getCategory() != null && product.getCategory().getId() != null) {
            Category dbCategory = this.categoryRepo.findById(product.getCategory().getId())
                    .orElseThrow(NotFoundException::new);
            product.setCategory(dbCategory);
        }

        Product toBeCreated = new Product();
        BeanUtils.copyProperties(product, toBeCreated, "id", "createdAt");

        return this.productRepo.save(toBeCreated);
    }

    public Product update(Long id, Product product) throws NotFoundException {
        Product existingProduct = this.findById(id);

        if (product.getCategory() != null && product.getCategory().getId() != null) {
            Category dbCategory = this.categoryRepo.findById(product.getCategory().getId())
                    .orElseThrow(NotFoundException::new);
            existingProduct.setCategory(dbCategory);
        }

        BeanUtils.copyProperties(product, existingProduct, "id", "createdAt", "category");

        return this.productRepo.save(existingProduct);
    }

    public void delete(Long id) throws UnexpectedException, NotFoundException {
        Optional<Product> found = this.productRepo.findById(id);

        if (found.isEmpty()) {
            throw new NotFoundException();
        }

        try {
            this.productRepo.delete(found.get());
        } catch (Exception e) {
            throw new UnexpectedException("Could not delete product with id: " + id);
        }
    }

}
