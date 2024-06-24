package com.example.RocketCart.repository;
import com.example.RocketCart.model.Cart;
import com.example.RocketCart.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Integer> {
}
