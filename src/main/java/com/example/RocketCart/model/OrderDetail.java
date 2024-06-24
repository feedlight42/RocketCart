package com.example.RocketCart.model;

import jakarta.persistence.*;

@Entity
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderDetailId;
    private Integer quantity;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private OrderTable order;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    public Integer getOrderDetailId() {
        return orderDetailId;
    }

    public void setOrderDetailId(Integer orderDetailId) {
        this.orderDetailId = orderDetailId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public OrderTable getOrder() {
        return order;
    }

    public void setOrder(OrderTable order) {
        this.order = order;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    // Getters and setters
}
