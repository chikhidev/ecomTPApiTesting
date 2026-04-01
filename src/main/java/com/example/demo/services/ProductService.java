package com.example.demo.services;

import java.rmi.UnexpectedException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.entities.Product;
import com.example.demo.repositories.ProductRepo;

@Service
public class ProductService {
    final private ProductRepo productRepo;

    public ProductService(ProductRepo productRepo) {
        this.productRepo = productRepo;
    }

    public List<Product> getAll() {
        return this.productRepo.findAll();
    }

    public Product findById(Long id) throws NotFoundException {
        Optional<Product> found = this.productRepo.findById(id);

        if (found.isPresent()) return found.get();

        throw new NotFoundException();
    }

    public Product create(Product product) {
        return this.productRepo.save(product);
    }

    public Product update(Long id, Product product) throws NotFoundException {
        Optional<Product> found = this.productRepo.findById(id);

        if (found.isPresent()) {
            BeanUtils.copyProperties(product, found.get());

            return this.productRepo.save(found.get());
        }

        throw new NotFoundException();
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
