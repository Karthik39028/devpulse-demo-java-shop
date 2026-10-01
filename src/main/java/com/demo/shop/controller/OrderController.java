package com.demo.shop.controller;

import com.demo.shop.model.Order;
import com.demo.shop.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST controller exposing CRUD endpoints for Order.
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Order> create(@RequestBody Order order) {
        Order created = service.createOrder(order);
        return ResponseEntity.status(201).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> get(@PathVariable Long id) {
        return service.getOrder(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Order> update(@PathVariable Long id, @RequestBody Order order) {
        try {
            Order updated = service.updateOrder(id, order);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<Order>> list(
            @RequestParam Optional<String> product,
            @RequestParam Optional<Order.OrderStatus> status,
            @RequestParam Optional<Double> minTotal) {
        List<Order> orders = service.listOrders(product, status, minTotal);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}/discount")
    public ResponseEntity<Double> discount(
            @PathVariable Long id,
            @RequestParam(required = false) String coupon,
            @RequestParam(defaultValue = "false") boolean loyal) {
        Optional<Order> maybe = service.getOrder(id);
        if (maybe.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        double disc = service.calculateDiscount(maybe.get(), coupon, loyal);
        return ResponseEntity.ok(disc);
    }
}
