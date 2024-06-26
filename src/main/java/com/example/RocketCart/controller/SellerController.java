package com.example.RocketCart.controller;

import com.example.RocketCart.model.Product;
import com.example.RocketCart.model.Seller;
import com.example.RocketCart.repository.ProductRepository;
import com.example.RocketCart.repository.SellerRepository;
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
@RequestMapping("/api/sellers")
public class SellerController {

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/{sellerId}")
    public ResponseEntity<Seller> getSellerDetails(@PathVariable int sellerId) {
        Optional<Seller> seller = sellerRepository.findById(sellerId);
        return seller.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{sellerId}")
    public ResponseEntity<Void> updateSellerProfile(@PathVariable int sellerId, @RequestBody Seller sellerDetails) {
        if (!sellerRepository.existsById(sellerId)) {
            return ResponseEntity.notFound().build();
        }
        sellerDetails.setSellerId(sellerId); // Ensure the seller id is set correctly
        sellerRepository.save(sellerDetails);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{sellerId}/products")
    public ResponseEntity<Page<Product>> getProductsBySeller(
            @PathVariable int sellerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

//        Pageable pageable = PageRequest.of(page, size, );
        Pageable pageable = PageRequest.of(page, size, Sort.by("productId").ascending());
        Page<Product> products = productRepository.findBySellerId(sellerId, pageable);

        if (products.hasContent()) {
            return ResponseEntity.ok(products);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping("/{sellerId}/products")
    public ResponseEntity<Product> addProductForSale(@PathVariable int sellerId, @RequestBody Product product) {
        product.setSellerId(sellerId); // Set the seller id for the product
        productRepository.save(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }



//    YET TO TEST ------------

//
    @PutMapping("/{sellerId}/products/{productId}")
    public ResponseEntity<Product> updateProduct(@PathVariable int sellerId, @PathVariable int productId, @RequestBody Product productDetails) {
        Product existingProduct = productRepository.findByProductIdAndSellerId(productId, sellerId);
        if (existingProduct == null) {
            return ResponseEntity.notFound().build();
        }
        productDetails.setProductId(productId);
        productDetails.setSellerId(sellerId);
        productRepository.save(productDetails);
        return ResponseEntity.ok(productDetails);
    }



    @DeleteMapping("/{sellerId}/products/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable int sellerId, @PathVariable int productId) {
        Product product = productRepository.findByProductIdAndSellerId(productId, sellerId);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        productRepository.delete(product);
        return ResponseEntity.noContent().build();
    }
}

