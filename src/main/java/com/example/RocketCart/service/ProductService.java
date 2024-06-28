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
        return productRepository.findAllByDeletedFalse(pageable);
    }

    public Page<Product> findAllByPriceAndCategory(Pageable pageable, Double minPrice, Double maxPrice, String categoryName) {
        if (minPrice != null && maxPrice != null && categoryName != null) {
            return productRepository.findByPriceBetweenAndCategoryNameContainingIgnoreCaseAndDeletedFalse(minPrice, maxPrice, categoryName, pageable);
        } else if (minPrice != null && maxPrice != null) {
            return productRepository.findByPriceBetweenAndDeletedFalse(minPrice, maxPrice, pageable);
        } else if (categoryName != null) {
            return productRepository.findByCategoryNameContainingIgnoreCaseAndDeletedFalse(categoryName, pageable);
        } else {
            return productRepository.findAllByDeletedFalse(pageable);
        }
    }

    public Page<Product> findAllByPriceCategoryAndSearchKeyword(Pageable pageable, Double minPrice, Double maxPrice, String categoryName, String searchKeyword) {
        if (searchKeyword != null && !searchKeyword.isEmpty()) {
            if (minPrice != null && maxPrice != null && categoryName != null) {
                return productRepository.findByPriceBetweenAndCategoryNameContainingIgnoreCaseAndProductNameContainingIgnoreCaseAndDeletedFalse(minPrice, maxPrice, categoryName, searchKeyword, pageable);
            } else if (minPrice != null && maxPrice != null) {
                return productRepository.findByPriceBetweenAndProductNameContainingIgnoreCaseAndDeletedFalse(minPrice, maxPrice, searchKeyword, pageable);
            } else if (categoryName != null) {
                return productRepository.findByCategoryNameContainingIgnoreCaseAndProductNameContainingIgnoreCaseAndDeletedFalse(categoryName, searchKeyword, pageable);
            } else {
                return productRepository.findByProductNameContainingIgnoreCaseAndDeletedFalse(searchKeyword, pageable);
            }
        } else {
            return findAllByPriceAndCategory(pageable, minPrice, maxPrice, categoryName);
        }
    }

    // Soft delete method
    public void softDeleteProduct(Integer productId) {
        productRepository.findById(productId).ifPresent(product -> {
            product.setDeleted(true);
            productRepository.save(product);
        });
    }

    // Restore soft deleted product
    public void restoreProduct(Integer productId) {
        productRepository.findById(productId).ifPresent(product -> {
            product.setDeleted(false);
            productRepository.save(product);
        });
    }

    // Check if a product is soft deleted
    public boolean isProductDeleted(Integer productId) {
        return productRepository.findById(productId)
                .map(Product::getDeleted)
                .orElse(true); // Assuming not found means deleted
    }
}
