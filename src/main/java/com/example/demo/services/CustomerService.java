package com.example.demo.services;

import java.rmi.UnexpectedException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.entities.Customer;
import com.example.demo.repositories.CustomerRepo;

/**
 * Service for managing Customer operations
 * @author aberrahimchikhi
 */
@Service
public class CustomerService {
    final private CustomerRepo customerRepo;

    public CustomerService(CustomerRepo customerRepo) {
        this.customerRepo = customerRepo;
    }

    public List<Customer> getAll() {
        return this.customerRepo.findAll();
    }

    public Customer findById(Long id) throws NotFoundException {
        Optional<Customer> found = this.customerRepo.findById(id);

        if (found.isPresent()) return found.get();

        throw new NotFoundException();
    }

    public List<Customer> findByEmail(String email) {
        return this.customerRepo.findByEmail(email);
    }

    public Customer create(Customer customer) {
        return this.customerRepo.save(customer);
    }

    public Customer update(Long id, Customer customer) throws NotFoundException {
        Optional<Customer> found = this.customerRepo.findById(id);

        if (found.isPresent()) {
            BeanUtils.copyProperties(customer, found.get(), "id", "createdAt");

            return this.customerRepo.save(found.get());
        }

        throw new NotFoundException();
    }

    public void delete(Long id) throws UnexpectedException, NotFoundException {
        Optional<Customer> found = this.customerRepo.findById(id);

        if (found.isEmpty()) {
            throw new NotFoundException();
        }
    
        try {
            this.customerRepo.delete(found.get());
        } catch (Exception e) {
            throw new UnexpectedException("Could not delete customer with id: " + id);
        }
    }

}
