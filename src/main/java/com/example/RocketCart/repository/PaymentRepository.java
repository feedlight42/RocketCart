package com.example.RocketCart.repository;
import com.example.RocketCart.model.Customer;
import com.example.RocketCart.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
}
