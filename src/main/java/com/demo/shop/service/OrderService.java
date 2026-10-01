package com.demo.shop.service;

import com.demo.shop.model.Order;
import com.demo.shop.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service layer containing business logic.
 */
@Service
public class OrderService {

    private final OrderRepository repository;

    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }

    /** Create a new order after basic validation. */
    public Order createOrder(Order order) {
        validateOrder(order);
        order.setStatus(Order.OrderStatus.NEW);
        return repository.save(order);
    }

    /** Retrieve an order by its identifier. */
    public Optional<Order> getOrder(Long id) {
        return repository.findById(id);
    }

    /** Update an existing order. */
    public Order updateOrder(Long id, Order updated) {
        Order existing = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + id));

        // Validation with multiple branches (high cyclomatic complexity)
        if (updated.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (updated.getPrice() < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }

        // Nested conditions for status transitions
        switch (existing.getStatus()) {
            case NEW:
                if (updated.getStatus() == Order.OrderStatus.CANCELED) {
                    existing.setStatus(Order.OrderStatus.CANCELED);
                } else {
                    existing.setStatus(updated.getStatus());
                }
                break;
            case PROCESSING:
                if (updated.getStatus() == Order.OrderStatus.COMPLETED) {
                    existing.setStatus(Order.OrderStatus.COMPLETED);
                } else if (updated.getStatus() == Order.OrderStatus.CANCELED) {
                    throw new IllegalStateException("Cannot cancel a processing order");
                }
                break;
            case COMPLETED:
                throw new IllegalStateException("Completed orders are immutable");
            case CANCELED:
                throw new IllegalStateException("Canceled orders are immutable");
        }

        // Apply other mutable fields
        existing.setCustomerName(updated.getCustomerName());
        existing.setProduct(updated.getProduct());
        existing.setQuantity(updated.getQuantity());
        existing.setPrice(updated.getPrice());

        return repository.save(existing);
    }

    /** Delete an order by id. */
    public void deleteOrder(Long id) {
        repository.deleteById(id);
    }

    /** List all orders with optional filters (demonstrates loops and conditionals). */
    public List<Order> listOrders(Optional<String> productFilter,
                                  Optional<Order.OrderStatus> statusFilter,
                                  Optional<Double> minTotal) {
        List<Order> all = repository.findAll();
        List<Order> result = new ArrayList<>();

        for (Order o : all) {
            boolean matches = true;
            if (productFilter.isPresent() && !o.getProduct().equalsIgnoreCase(productFilter.get())) {
                matches = false;
            }
            if (statusFilter.isPresent() && o.getStatus() != statusFilter.get()) {
                matches = false;
            }
            if (minTotal.isPresent() && o.totalAmount() < minTotal.get()) {
                matches = false;
            }
            if (matches) {
                result.add(o);
            }
        }

        result.sort(Comparator.comparing(Order::getOrderDate).reversed());
        return result;
    }

    /** Example of a long method with many branches – calculates discounts. */
    public double calculateDiscount(Order order, String couponCode, boolean isLoyalCustomer) {
        double discount = 0.0;
        double total = order.totalAmount();
        if (total > 1000) {
            discount += 0.10;
        } else if (total > 500) {
            discount += 0.05;
        }

        if (couponCode != null && !couponCode.isBlank()) {
            if (couponCode.equalsIgnoreCase("SPRING20")) {
                discount += 0.20;
            } else if (couponCode.equalsIgnoreCase("WELCOME5")) {
                discount += 0.05;
            } else if (couponCode.startsWith("VIP")) {
                if (isLoyalCustomer) {
                    discount += 0.15;
                }
            }
        }

        if (isLoyalCustomer) {
            discount += 0.02;
        }
        if (discount > 0.30) {
            discount = 0.30;
        }
        return total * discount;
    }

    /** Internal validation helper. */
    private void validateOrder(Order order) {
        List<String> errors = new ArrayList<>();
        if (order.getCustomerName() == null || order.getCustomerName().isBlank()) {
            errors.add("Customer name is required");
        }
        if (order.getProduct() == null || order.getProduct().isBlank()) {
            errors.add("Product is required");
        }
        if (order.getQuantity() <= 0) {
            errors.add("Quantity must be greater than zero");
        }
        if (order.getPrice() < 0) {
            errors.add("Price cannot be negative");
        }
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Invalid order: " + String.join("; ", errors));
        }
    }
}
