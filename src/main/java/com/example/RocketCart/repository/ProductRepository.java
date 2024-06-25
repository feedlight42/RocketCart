package com.example.RocketCart.repository;
import com.example.RocketCart.model.Customer;
import com.example.RocketCart.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {


    List<Product> findBySellerId(int sellerId);

    Product findByProductIdAndSellerId(int productId, int sellerId);

    Page<Product> findByPriceBetweenAndCategoryName(Double minPrice, Double maxPrice, String categoryName, Pageable pageable);

    Page<Product> findByPriceBetween(Double minPrice, Double maxPrice, Pageable pageable);

    Page<Product> findByCategoryName(String categoryName, Pageable pageable);

    Page<Product> findByPriceBetweenAndCategoryNameContaining(Double minPrice, Double maxPrice, String categoryName, Pageable pageable);

    Page<Product> findByCategoryNameContaining(String categoryName, Pageable pageable);
}
