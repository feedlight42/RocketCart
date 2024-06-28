package com.example.RocketCart.repository;

import com.example.RocketCart.model.Product;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    // Soft deletion queries
    Page<Product> findByPriceBetweenAndCategoryNameContainingIgnoreCaseAndProductNameContainingIgnoreCaseAndDeletedFalse(
            Double minPrice, Double maxPrice, String categoryName, String productName, Pageable pageable);

    Page<Product> findByPriceBetweenAndProductNameContainingIgnoreCaseAndDeletedFalse(
            Double minPrice, Double maxPrice, String productName, Pageable pageable);

    Page<Product> findByCategoryNameContainingIgnoreCaseAndProductNameContainingIgnoreCaseAndDeletedFalse(
            String categoryName, String productName, Pageable pageable);

    Page<Product> findByProductNameContainingIgnoreCaseAndDeletedFalse(
            String productName, Pageable pageable);

    Page<Product> findBySellerIdAndDeletedFalse(
            int sellerId, Pageable pageable);

    Product findByProductIdAndSellerIdAndDeletedFalse(
            int productId, int sellerId);

    Page<Product> findByPriceBetweenAndCategoryNameAndDeletedFalse(
            Double minPrice, Double maxPrice, String categoryName, Pageable pageable);

    Page<Product> findByPriceBetweenAndDeletedFalse(
            Double minPrice, Double maxPrice, Pageable pageable);

    Page<Product> findByCategoryNameAndDeletedFalse(
            String categoryName, Pageable pageable);

    Page<Product> findByPriceBetweenAndCategoryNameContainingAndDeletedFalse(
            Double minPrice, Double maxPrice, String categoryName, Pageable pageable);

    Page<Product> findByCategoryNameContainingAndDeletedFalse(
            String categoryName, Pageable pageable);

    Page<Product> findByPriceBetweenAndCategoryNameContainingAndProductNameContainingAndDeletedFalse(
            Double minPrice, Double maxPrice, String categoryName, String searchKeyword, Pageable pageable);

    Page<Product> findByPriceBetweenAndProductNameContainingAndDeletedFalse(
            Double minPrice, Double maxPrice, String searchKeyword, Pageable pageable);

    Page<Product> findByCategoryNameContainingAndProductNameContainingAndDeletedFalse(
            String categoryName, String searchKeyword, Pageable pageable);

    Page<Product> findBySellerIdAndProductNameContainingAndDeletedFalse(
            int sellerId, String searchKeyword, Pageable pageable);


    Optional<Product> findByProductIdAndDeletedFalse(Integer productId);


    List<Product> findProductBySellerId(Integer sellerId);


    Page<Product> findAllByDeletedFalse(Pageable pageable);

    Page<Product> findByPriceBetweenAndCategoryNameContainingIgnoreCaseAndDeletedFalse(Double minPrice, Double maxPrice, String categoryName, Pageable pageable);

    Page<Product> findByCategoryNameContainingIgnoreCaseAndDeletedFalse(String categoryName, Pageable pageable);
}

