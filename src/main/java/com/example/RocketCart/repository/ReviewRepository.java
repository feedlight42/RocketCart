package com.example.RocketCart.repository;
import com.example.RocketCart.model.Customer;
import com.example.RocketCart.model.Product;
import com.example.RocketCart.model.Review;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findByProductId(Integer productId);

    List<Review> findByCustomer(Customer customer);

    boolean existsByCustomerAndProductId(Customer customer, Integer productId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.productId = :productId")
    Double findAverageRatingByProductId(int productId);

    List<Review> findAllByDeletedFalse();

    Optional<Review> findByReviewIdAndDeletedFalse(Integer reviewId);

//    boolean existsByCustomerAndProductId(Customer customer, Integer productId);
}
