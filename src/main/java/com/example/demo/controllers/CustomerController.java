package com.example.demo.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entities.Customer;
import com.example.demo.services.CustomerService;

import java.rmi.UnexpectedException;
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
 * REST Controller for Customer CRUD operations
 * @author aberrahimchikhi
 */
@RestController
@RequestMapping("customers")
public class CustomerController {

    final private CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping()
    public List<Customer> getAllCustomers() {
        return this.customerService.getAll();
    }

    @GetMapping("{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) throws NotFoundException {
        Customer customer = this.customerService.findById(id);
        return ResponseEntity.status(200).body(customer);
    }

    @PostMapping()
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {
        Customer created = this.customerService.create(customer);

        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Long id, @RequestBody Customer payload)
            throws NotFoundException
    {
        Customer updated = this.customerService.update(id, payload);
        return ResponseEntity.status(200).body(updated);
    }

    @PatchMapping("{id}")
    public ResponseEntity<Customer> updatePartialCustomer(@PathVariable Long id, @RequestBody Customer payload)
            throws NotFoundException
    {
        Customer updated = this.customerService.update(id, payload);
        return ResponseEntity.status(200).body(updated);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Customer> deleteCustomer(@PathVariable Long id)
            throws NotFoundException, UnexpectedException
    {

        this.customerService.delete(id);
        return ResponseEntity.status(204).build();
    }

    // ERROR HANDLERS

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleException(NotFoundException ex) {
        return ResponseEntity.status(404).body("Could not find this customer");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleOtherExceptions(Exception ex) {
        return ResponseEntity.status(500).body("Something went wrong: " + ex.getMessage());
    }

}
