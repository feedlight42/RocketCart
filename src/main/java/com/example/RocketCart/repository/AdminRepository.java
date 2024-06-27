package com.example.RocketCart.repository;
import com.example.RocketCart.model.Admin;
import com.example.RocketCart.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Integer> {
    Admin findByUsername(String username);
}
