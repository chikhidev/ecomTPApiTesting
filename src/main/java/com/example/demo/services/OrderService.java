package com.example.demo.services;

import java.rmi.UnexpectedException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.entities.Order;
import com.example.demo.entities.OrderItem;
import com.example.demo.entities.Product;
import com.example.demo.repositories.OrderRepo;
import com.example.demo.repositories.ProductRepo;

/**
 * Service for managing Order operations with business logic
 * @author aberrahimchikhi
 */
@Service
public class OrderService {
    final private OrderRepo orderRepo;
    final private ProductRepo productRepo;

    public OrderService(OrderRepo orderRepo, ProductRepo productRepo) {
        this.orderRepo = orderRepo;
        this.productRepo = productRepo;
    }

    public List<Order> getAll() {
        return this.orderRepo.findAll();
    }

    public Order findById(Long id) throws NotFoundException {
        Optional<Order> found = this.orderRepo.findById(id);

        if (found.isPresent()) return found.get();

        throw new NotFoundException();
    }

    public Order create(Order order) throws IllegalArgumentException, UnexpectedException, NotFoundException {
        try {
            if (order.getOrderItems() != null) {
                for (OrderItem item : order.getOrderItems()) {
                    Product product = item.getProduct();
                    if (product == null) {
                        throw new IllegalArgumentException("Order item must contain a valid product");
                    }

                    Optional<Product> productOpt = this.productRepo.findById(product.getId());
                    if (productOpt.isEmpty()) {
                        throw new NotFoundException();
                    }

                    Product dbProduct = productOpt.get();
                    if (dbProduct.getStockQuantity() < item.getQuantity()) {
                        throw new IllegalArgumentException(
                            "Insufficient stock for product: " + dbProduct.getName() + 
                            ". Available: " + dbProduct.getStockQuantity() + 
                            ", Requested: " + item.getQuantity()
                        );
                    }
                }
            }

            if (order.getStatus() == null) {
                order.setStatus(Order.OrderStatus.PENDING);
            }

            order.calculateTotal();

            Order savedOrder = this.orderRepo.save(order);

            if (savedOrder.getOrderItems() != null) {
                for (OrderItem item : savedOrder.getOrderItems()) {
                    Product product = this.productRepo.findById(item.getProduct().getId()).get();
                    product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
                    this.productRepo.save(product);
                }
            }

            return savedOrder;
        } catch (NotFoundException e) {
            throw e;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new UnexpectedException("Error creating order: " + e.getMessage());
        }
    }

    public Order update(Long id, Order orderUpdate) throws NotFoundException {
        Optional<Order> found = this.orderRepo.findById(id);

        if (found.isPresent()) {
            Order order = found.get();
            
            if (orderUpdate.getStatus() != null) {
                order.setStatus(orderUpdate.getStatus());
            }

            return this.orderRepo.save(order);
        }

        throw new NotFoundException();
    }

    public Order updateStatus(Long id, Order.OrderStatus status) throws NotFoundException {
        Optional<Order> found = this.orderRepo.findById(id);

        if (found.isPresent()) {
            Order order = found.get();
            order.setStatus(status);
            return this.orderRepo.save(order);
        }

        throw new NotFoundException();
    }

    public void delete(Long id) throws UnexpectedException, NotFoundException {
        Optional<Order> found = this.orderRepo.findById(id);

        if (found.isEmpty()) {
            throw new NotFoundException();
        }
    
        try {
            Order order = found.get();
            
            if (order.getStatus() == Order.OrderStatus.PENDING && order.getOrderItems() != null) {
                for (OrderItem item : order.getOrderItems()) {
                    Product product = this.productRepo.findById(item.getProduct().getId()).get();
                    product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
                    this.productRepo.save(product);
                }
            }

            this.orderRepo.delete(order);
        } catch (Exception e) {
            throw new UnexpectedException("Could not delete order with id: " + id);
        }
    }

}
