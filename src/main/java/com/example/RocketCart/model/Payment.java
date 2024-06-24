package com.example.RocketCart.model;
import jakarta.persistence.*;
import java.util.Date;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer paymentId;
    private Date paymentDate;
    private String paymentMethod;
    private Double amount;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private OrderTable order;

    public Integer getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Integer paymentId) {
        this.paymentId = paymentId;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public OrderTable getOrder() {
        return order;
    }

    public void setOrder(OrderTable order) {
        this.order = order;
    }

    // Getters and setters
}
