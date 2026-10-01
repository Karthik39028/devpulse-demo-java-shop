package com.demo.shop.repository;

import com.demo.shop.model.Order;
import java.util.List;
import java.util.Optional;

/**
 * Simple in‑memory repository interface.
 * In a real project this would extend Spring Data JPA interfaces.
 */
public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(Long id);
    List<Order> findAll();
    void deleteById(Long id);
}
