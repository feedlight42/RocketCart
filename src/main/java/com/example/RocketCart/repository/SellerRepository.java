package com.example.RocketCart.repository;
import com.example.RocketCart.model.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SellerRepository extends JpaRepository<Seller, Integer> {
    Seller findByUsername(String username);

    List<Seller> findAllByDeletedFalse();

    Optional<Seller> findBySellerIdAndDeletedFalse(Integer sellerId);
}
