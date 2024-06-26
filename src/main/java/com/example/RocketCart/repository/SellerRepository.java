package com.example.RocketCart.repository;
import com.example.RocketCart.model.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerRepository extends JpaRepository<Seller, Integer> {
    Seller findByCompanyName(String username);
}
