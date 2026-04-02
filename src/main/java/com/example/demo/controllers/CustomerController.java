package com.example.demo.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entities.Customer;
import com.example.demo.services.CustomerService;

import java.rmi.UnexpectedException;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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

    private static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

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

    @Validated
    @PostMapping()
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer)
        throws InvalidParameterException
    {
        if (customer.getId() != null) throw new InvalidParameterException("ID must not be provided on creation");
        if (customer.getFirstName() == null || customer.getFirstName().length() == 0 || customer.getLastName() == null || customer.getLastName().length() == 0)
                throw new InvalidParameterException("Invalid firstName or lastName");

        if (customer.getEmail() == null || !EMAIL_PATTERN.matcher(customer.getEmail()).matches()) {
            throw new InvalidParameterException("Invalid email format");
        }

        List<Customer> alreadyExistEmailList = this.customerService.findByEmail(customer.getEmail());

        if (alreadyExistEmailList.size() > 0) throw new InvalidParameterException("Email already exist");

        Customer created = this.customerService.create(customer);

        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Long id, @RequestBody Customer payload)
            throws NotFoundException, InvalidParameterException
    {
        if (payload.getFirstName() == null || payload.getFirstName().length() == 0 || payload.getLastName() == null || payload.getLastName().length() == 0)
                throw new InvalidParameterException("Invalid firstName or lastName");

        if (payload.getEmail() == null || !EMAIL_PATTERN.matcher(payload.getEmail()).matches()) {
            throw new InvalidParameterException("Invalid email format");
        }

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
