package com.example.RocketCart.controller;

import com.example.RocketCart.model.Product;
import com.example.RocketCart.model.Review;
import com.example.RocketCart.repository.ProductRepository;
import com.example.RocketCart.repository.ReviewRepository;
import com.example.RocketCart.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class ProductController {

    private final ProductRepository productRepository;
    private final ProductService productService;
    private final ReviewRepository reviewRepository;

    @Autowired
    public ProductController(ProductRepository productRepository, ProductService productService, ReviewRepository reviewRepository) {
        this.productRepository = productRepository;
        this.productService = productService;
        this.reviewRepository = reviewRepository;
    }

//    @GetMapping("/api/products")
//    public List<Product> listProducts() {
//
//            return productRepository.findAll();
//
//    }

    @GetMapping("/api/products")
    public Page<Product> searchProducts(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10000000") int size,
                                        @RequestParam(required = false) Double minPrice,
                                        @RequestParam(required = false) Double maxPrice,
                                        @RequestParam(required = false) String categoryName,
                                        @RequestParam(required = false) String searchKeyword,
                                        @RequestParam(defaultValue = "-1") Integer sort) {

//        Pageable pageable;
        Pageable pageable = PageRequest.of(page, size);
        System.out.println(sort);
        if (sort == -1) {
            return productService.findAllByPriceCategoryAndSearchKeyword(pageable, minPrice, maxPrice, categoryName, searchKeyword);
        }

        Sort.Direction sortDirection = (sort == 1) ? Sort.Direction.DESC : Sort.Direction.ASC;
        pageable = PageRequest.of(page, size, Sort.by(sortDirection, "price"));


        return productService.findAllByPriceCategoryAndSearchKeyword(pageable, minPrice, maxPrice, categoryName, searchKeyword);
    }


//    @GetMapping("/api/products")
//    public Page<Product> searchProducts(@RequestParam(defaultValue = "0") int page,
//                                        @RequestParam(defaultValue = "10000000") int size,
//                                        @RequestParam(required = false) Double minPrice,
//                                        @RequestParam(required = false) Double maxPrice,
//                                        @RequestParam(required = false) String categoryName,
//                                        @RequestParam(required = false) String searchKeyword) {
//        Pageable pageable = PageRequest.of(page, size);
//
//        return productService.findAllByPriceCategoryAndSearchKeyword(pageable, minPrice, maxPrice, categoryName, searchKeyword);
////
////        if (minPrice == null && maxPrice == null && categoryName == null && searchKeyword == null) {
////            return productService.getAllProducts(pageable);
////        } else if (searchKeyword != null && !searchKeyword.isEmpty()) {
////            return productService.findAllByPriceCategoryAndSearchKeyword(pageable, minPrice, maxPrice, categoryName, searchKeyword);
////        } else {
////            return productService.findAllByPriceAndCategory(pageable, minPrice, maxPrice, categoryName);
////        }
//    }


    @GetMapping("/api/products/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable Integer productId) {

        Optional<Product> productOptional = productRepository.findById(productId);
        return productOptional.map(product -> new ResponseEntity<>(product, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));

    }

    @GetMapping("/api/products/{productId}/reviews")
    public ResponseEntity<List<Review>> getReviewsForProduct(@PathVariable Integer productId) {
        Optional<Product> productOptional = productRepository.findById(productId);

        if (productOptional.isPresent()) {
            List<Review> reviews = reviewRepository.findByProductId(productId);
            return new ResponseEntity<>(reviews, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/api/products/{productId}/reviews")
    public ResponseEntity<Review> createReviewForProduct(@PathVariable Integer productId, @RequestBody Review review) {
        Optional<Product> productOptional = productRepository.findById(productId);

        if (productOptional.isPresent()) {
//            Product product = productOptional.get();
            review.setProductId(productId);

            Review newReview = reviewRepository.save(review);
            return new ResponseEntity<>(newReview, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }




}
