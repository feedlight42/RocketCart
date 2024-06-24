package com.example.RocketCart.repository;
import com.example.RocketCart.model.Customer;
import com.example.RocketCart.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
}
