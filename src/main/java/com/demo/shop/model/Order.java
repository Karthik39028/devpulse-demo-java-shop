package com.demo.shop.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Order {
    private Long id;
    private String customerName;
    private String product;
    private int quantity;
    private double price;
    private LocalDateTime orderDate;
    private OrderStatus status;

    public enum OrderStatus {
        NEW, PROCESSING, COMPLETED, CANCELED
    }

    public Order() {
        this.orderDate = LocalDateTime.now();
        this.status = OrderStatus.NEW;
    }

    public Order(Long id, String customerName, String product, int quantity, double price) {
        this();
        this.id = id;
        this.customerName = customerName;
        this.product = product;
        this.quantity = quantity;
        this.price = price;
    }

    // Getters and setters ---------------------------------------------------
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getProduct() { return product; }
    public void setProduct(String product) { this.product = product; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public double totalAmount() { return quantity * price; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order)) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "Order{id=" + id +
                ", customerName='" + customerName + '\'' +
                ", product='" + product + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", orderDate=" + orderDate +
                ", status=" + status +
                '}';
    }
}
