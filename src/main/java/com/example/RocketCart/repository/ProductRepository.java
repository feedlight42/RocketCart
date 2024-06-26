package com.example.RocketCart.repository;
import com.example.RocketCart.model.Customer;
import com.example.RocketCart.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    Page<Product> findByPriceBetweenAndCategoryNameContainingIgnoreCaseAndProductNameContainingIgnoreCase(Double minPrice, Double maxPrice, String categoryName, String productName, Pageable pageable);
    Page<Product> findByPriceBetweenAndProductNameContainingIgnoreCase(Double minPrice, Double maxPrice, String productName, Pageable pageable);
    Page<Product> findByCategoryNameContainingIgnoreCaseAndProductNameContainingIgnoreCase(String categoryName, String productName, Pageable pageable);
    Page<Product> findByProductNameContainingIgnoreCase(String productName, Pageable pageable);

//    List<Product> findBySellerId(int sellerId);

    Page<Product> findBySellerId(int sellerId, Pageable pageable);

    Product findByProductIdAndSellerId(int productId, int sellerId);

    Page<Product> findByPriceBetweenAndCategoryName(Double minPrice, Double maxPrice, String categoryName, Pageable pageable);

    Page<Product> findByPriceBetween(Double minPrice, Double maxPrice, Pageable pageable);

    Page<Product> findByCategoryName(String categoryName, Pageable pageable);

    Page<Product> findByPriceBetweenAndCategoryNameContaining(Double minPrice, Double maxPrice, String categoryName, Pageable pageable);

    Page<Product> findByCategoryNameContaining(String categoryName, Pageable pageable);

    Page<Product> findByPriceBetweenAndCategoryNameContainingAndProductNameContaining(Double minPrice, Double maxPrice, String categoryName, String searchKeyword, Pageable pageable);

    Page<Product> findByPriceBetweenAndProductNameContaining(Double minPrice, Double maxPrice, String searchKeyword, Pageable pageable);

    Page<Product> findByCategoryNameContainingAndProductNameContaining(String categoryName, String searchKeyword, Pageable pageable);

    Page<Product> findBySellerIdAndProductNameContaining(int sellerId, String searchKeyword, Pageable pageable);
}
