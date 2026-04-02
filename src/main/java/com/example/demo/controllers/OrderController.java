package com.example.demo.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entities.Order;
import com.example.demo.services.OrderService;

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
 * REST Controller for Order CRUD operations
 * @author aberrahimchikhi
 */
@RestController
@RequestMapping("orders")
public class OrderController {

    final private OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping()
    public List<Order> getAllOrders() {
        return this.orderService.getAll();
    }

    @GetMapping("{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) throws NotFoundException {
        Order order = this.orderService.findById(id);
        return ResponseEntity.status(200).body(order);
    }

    @PostMapping()
    public ResponseEntity<Order> createOrder(@RequestBody Order order) 
            throws IllegalArgumentException, UnexpectedException, NotFoundException {
        if (order.getId() != null) throw new IllegalArgumentException("ID must not be provided on creation");
        Order created = this.orderService.create(order);

        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable Long id, @RequestBody Order payload)
            throws NotFoundException
    {
        Order updated = this.orderService.update(id, payload);
        return ResponseEntity.status(200).body(updated);
    }

    @PatchMapping("{id}")
    public ResponseEntity<Order> updatePartialOrder(@PathVariable Long id, @RequestBody Order payload)
            throws NotFoundException
    {
        Order updated = this.orderService.update(id, payload);
        return ResponseEntity.status(200).body(updated);
    }

    @PatchMapping("{id}/status")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Long id, @RequestBody StatusUpdate statusUpdate)
            throws NotFoundException, IllegalArgumentException
    {
        if (statusUpdate.status == null || statusUpdate.status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status must be provided. Valid values: PENDING, COMPLETED, CANCELLED");
        }
        try {
            Order.OrderStatus status = Order.OrderStatus.valueOf(statusUpdate.status.toUpperCase());
            Order updated = this.orderService.updateStatus(id, status);
            return ResponseEntity.status(200).body(updated);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status. Valid values: PENDING, COMPLETED, CANCELLED");
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Order> deleteOrder(@PathVariable Long id)
            throws NotFoundException, UnexpectedException
    {

        this.orderService.delete(id);
        return ResponseEntity.status(204).build();
    }

    // ERROR HANDLERS

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(NotFoundException ex) {
        return ResponseEntity.status(404).body("NOT FOUND");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(400).body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleOtherExceptions(Exception ex) {
        return ResponseEntity.status(500).body("Something went wrong: " + ex.getMessage());
    }







    public static class StatusUpdate {
        public String status;

        public StatusUpdate() {}

        public StatusUpdate(String status) {
            this.status = status;
        }
    }

}
