package com.example.RocketCart.service;

import com.example.RocketCart.model.Product;
import com.example.RocketCart.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public Page<Product> findAllByPriceAndCategory(Pageable pageable, Double minPrice, Double maxPrice, String categoryName) {
        if (minPrice != null && maxPrice != null && categoryName != null) {
            return productRepository.findByPriceBetweenAndCategoryNameContaining(minPrice, maxPrice, categoryName, pageable);
        } else if (minPrice != null && maxPrice != null) {
            return productRepository.findByPriceBetween(minPrice, maxPrice, pageable);
        } else if (categoryName != null) {
            return productRepository.findByCategoryNameContaining(categoryName, pageable);
        } else {
            return productRepository.findAll(pageable);
        }
    }


}
