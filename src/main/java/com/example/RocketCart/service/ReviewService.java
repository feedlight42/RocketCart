package com.example.RocketCart.service;

import com.example.RocketCart.model.Customer;
import com.example.RocketCart.model.Product;
import com.example.RocketCart.model.Review;
import com.example.RocketCart.repository.CustomerRepository;
import com.example.RocketCart.repository.ProductRepository;
import com.example.RocketCart.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository, CustomerRepository customerRepository, ProductRepository productRepository) {
        this.reviewRepository = reviewRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }

    public List<Review> getAllReviews() {
        return reviewRepository.findAllByDeletedFalse();
    }

    public Optional<Review> getReviewById(Integer reviewId) {
        return reviewRepository.findByReviewIdAndDeletedFalse(reviewId);
    }

    public Review createReview(Review review) {
        return reviewRepository.save(review);
    }

    public Review updateReview(Integer reviewId, Review updatedReview) {
        Optional<Review> reviewOptional = reviewRepository.findByReviewIdAndDeletedFalse(reviewId);
        if (reviewOptional.isPresent()) {
            updatedReview.setReviewId(reviewId);
            return reviewRepository.save(updatedReview);
        } else {
            return null;
        }
    }

    public void deleteReview(Integer reviewId) {
        Optional<Review> reviewOptional = reviewRepository.findByReviewIdAndDeletedFalse(reviewId);
        reviewOptional.ifPresent(review -> reviewRepository.delete(review));
    }

    public void softDeleteReview(Integer reviewId) {
        Optional<Review> reviewOptional = reviewRepository.findByReviewIdAndDeletedFalse(reviewId);
        if (reviewOptional.isPresent()) {
            Review review = reviewOptional.get();
            review.setDeleted(true);
            reviewRepository.save(review);
        }
    }

    public List<?> getAllActiveReviews() {
        return reviewRepository.findAllByDeletedFalse();
    }

    public List<Review> getAllReviewsForCustomer(Integer customerId) {
        Optional<Customer> customerOptional = customerRepository.findById(customerId);
        if (customerOptional.isPresent()) {
            Customer customer = customerOptional.get();
            return reviewRepository.findByCustomer(customer);
        }
        return List.of(); // Return an empty list if customer is not found
    }

    public Review addReviewForCustomer(Integer customerId, Integer productId, Review review) {
        Optional<Customer> customerOptional = customerRepository.findById(customerId);
        Optional<Product> productOptional = productRepository.findById(productId);

        if (customerOptional.isPresent() && productOptional.isPresent()) {
            Customer customer = customerOptional.get();
            Product product = productOptional.get();

            review.setCustomer(customer);
            review.setProductId(product.getProductId());

            // Assuming other logic for setting review details like rating, comment, etc.

            return reviewRepository.save(review);
        } else {
            throw new IllegalArgumentException("Customer or Product not found!");
        }
    }


    public boolean checkReviewExists(Integer customerId, Integer productId) {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        Product product = productRepository.findById(productId).orElse(null);

        if (customer != null && product != null) {
            return reviewRepository.existsByCustomerAndProductId(customer, product.getProductId());
        }

        return false;
    }
}
