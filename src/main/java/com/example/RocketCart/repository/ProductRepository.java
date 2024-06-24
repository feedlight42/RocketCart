package com.example.RocketCart.repository;
import com.example.RocketCart.model.Customer;
import com.example.RocketCart.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {
}
