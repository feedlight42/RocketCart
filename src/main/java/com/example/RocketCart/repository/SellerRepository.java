package com.example.RocketCart.repository;
import com.example.RocketCart.model.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SellerRepository extends JpaRepository<Seller, Integer> {
    Seller findByUsername(String username);

    List<Seller> findAllByDeletedFalse();

    Optional<Seller> findBySellerIdAndDeletedFalse(Integer sellerId);

    List<Seller> findAllByVerifiedTrue();

    List<Seller> findAllByVerifiedFalse();

    @Query("SELECT s.verified FROM Seller s WHERE s.sellerId = :sellerId")
    boolean findVerifiedStatusBySellerId(@Param("sellerId") Integer sellerId);
}
