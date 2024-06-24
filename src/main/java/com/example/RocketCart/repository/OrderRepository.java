package com.example.RocketCart.repository;
import com.example.RocketCart.model.Customer;
import com.example.RocketCart.model.OrderTable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderTable, Integer> {
}
