package com.example.RocketCart.controller;

import com.example.RocketCart.exceptions.InsufficientStockException;
import com.example.RocketCart.model.*;
import com.example.RocketCart.repository.CartRepository;
import com.example.RocketCart.repository.OrderDetailRepository;
import com.example.RocketCart.service.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class CustomerController {

    private final CustomerService customerService;
    private final CartRepository cartRepository;
    private final CartService cartService;
    private final OrderTableService orderService;
    private final ReviewService reviewService;
    private final SellerService sellerService;
    private final OrderDetailService orderDetailService;

    @Autowired
    public CustomerController(CustomerService customerService, CartService cartService,
                              OrderTableService orderService, ProductService productService, CartRepository cartRepository,
                              ReviewService reviewService, SellerService sellerService, OrderDetailService orderDetailService) {
        this.customerService = customerService;
        this.cartService = cartService;
        this.orderService = orderService;
        this.cartRepository = cartRepository;
        this.reviewService = reviewService;
        this.sellerService = sellerService;
        this.orderDetailService = orderDetailService;
    }

    @GetMapping("/api/c/{customerId}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Integer customerId) {
        Optional<Customer> customerOptional = customerService.getCustomerById(customerId);
        return customerOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/api/s/{id}")
    public ResponseEntity<Seller> getSellerById(@PathVariable Integer id) {
        Optional<Seller> seller = sellerService.getSellerById(id);
        return seller.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{customerId}/uploadProfilePicture")
    public ResponseEntity<?> uploadProfilePicture(@PathVariable Integer customerId, @RequestParam("file") MultipartFile file) {
        try {
            boolean success = customerService.uploadProfilePicture(customerId, file);
            if (success) {
                return ResponseEntity.ok().body("Profile picture uploaded successfully for customer ID: " + customerId);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload profile picture");
        }
    }

    @GetMapping("/{customerId}/profilePicture")
    public ResponseEntity<byte[]> getProfilePicture(@PathVariable Integer customerId) {
        Optional<Customer> customerOptional = customerService.getCustomerById(customerId);
        if (customerOptional.isPresent()) {
            byte[] imageData = customerService.getProfilePicture(customerId);
            if (imageData != null) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(imageData);
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/api/customers")
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {
        Customer newCustomer = customerService.createCustomer(customer);
        return new ResponseEntity<>(newCustomer, HttpStatus.CREATED);
    }

    @GetMapping("/api/login/{customerId}")
    public ResponseEntity<Customer> loginCustomer(@PathVariable Integer customerId) {
        Optional<Customer> customerOptional = customerService.getCustomerById(customerId);
        return customerOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/api/customers/{customerId}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Integer customerId, @RequestBody Customer updatedCustomer) {
        Optional<Customer> existingCustomerOptional = customerService.getCustomerById(customerId);
        if (existingCustomerOptional.isPresent()) {
            Customer updatedCustomerEntity = customerService.updateCustomer(customerId, updatedCustomer);
            return ResponseEntity.ok(updatedCustomerEntity);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/api/customers/{customerId}/cart")
    public ResponseEntity<List<Cart>> getCustomerCart(@PathVariable Integer customerId) {
        List<Cart> cartItems = cartService.getCustomerCart(customerId);
        return ResponseEntity.ok(cartItems);
    }

    @PostMapping("/api/customers/{customerId}/cart")
    public ResponseEntity<Cart> addItemToCart(@PathVariable Integer customerId, @RequestBody Cart cartItem) {
        Cart newCartItem = cartService.addItemToCart(customerId, cartItem);
        return new ResponseEntity<>(newCartItem, HttpStatus.CREATED);
    }

    @DeleteMapping("/api/customers/{customerId}/cart/{cartItemId}")
    public ResponseEntity<String> deleteCartItem(@PathVariable Integer cartItemId) {
        boolean deleted = cartService.softDeleteCartItem(cartItemId);
        if (deleted) {
            return ResponseEntity.ok("Cart item deleted successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/api/customers/{customerId}/cart/{cartId}")
    public ResponseEntity<Cart> updateCartItem(@PathVariable Integer customerId, @PathVariable Integer cartId, @RequestBody Cart updatedCart) {
        Cart updatedCartItem = cartService.updateCartItem(customerId, cartId, updatedCart);
        if (updatedCartItem != null) {
            return ResponseEntity.ok(updatedCartItem);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/api/customers/{customerId}/cart/{cartId}")
    public ResponseEntity<Cart> updateCartItemQuantity(
            @PathVariable Long customerId,
            @PathVariable Integer cartId,
            @RequestBody Map<String, Object> updatedFields) {

        // Check if the cart item exists
        Optional<Cart> cartOptional = cartRepository.findById(cartId);
        if (cartOptional.isPresent()) {
            Cart existingCart = cartOptional.get();

            // Update fields from request body
            if (updatedFields.containsKey("quantity")) {
                existingCart.setQuantity((Integer) updatedFields.get("quantity"));
            }

            // Save the updated cart item
            Cart updatedCartItem = cartRepository.save(existingCart);
            return ResponseEntity.ok(updatedCartItem);
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/api/customers/{customerId}/orderhistory")
    public List<OrderTable> getOrderHistory(@PathVariable Integer customerId) {
        return orderService.getOrderHistory(customerId);
    }

    @GetMapping("/api/customers/{customerId}/orderhistory/{orderId}")
    public ResponseEntity<List<OrderDetail>> getOrderDetailsByOrderId(@PathVariable Integer orderId) {
        List<OrderDetail> orderDetails = orderDetailService.getOrderDetailsByOrderId(orderId);
        if (!orderDetails.isEmpty()) {
            return ResponseEntity.ok(orderDetails);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

//    @Transactional
//    @PostMapping("/api/customers/{customerId}/payment")
//    public ResponseEntity<String> placeOrderAndMakePayment(@PathVariable Integer customerId, @RequestBody Payment paymentRequest) throws InsufficientStockException {
//        try {
//            orderService.placeOrderAndMakePayment(customerId, paymentRequest);
//            return ResponseEntity.ok("Order placed and payment made successfully");
//        } catch (InsufficientStockException e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//        }
//    }





    @PostMapping("/api/customers/{customerId}/make-order")
    public ResponseEntity<OrderTable> placeOrder(@PathVariable int customerId) {
        try {
            OrderTable order = orderService.placeOrder(customerId);
            return ResponseEntity.ok(order);
        } catch (InsufficientStockException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
    }

















    // Customer side review endpoints
    @GetMapping("/api/customers/{customerId}/reviews")
    public ResponseEntity<List<Review>> getAllReviewsForCustomer(@PathVariable Integer customerId) {
        List<Review> reviews = reviewService.getAllReviewsForCustomer(customerId);
        return ResponseEntity.ok(reviews);
    }

    @PostMapping("/api/customers/{customerId}/products/{productId}/reviews")
    public ResponseEntity<Review> addReviewForCustomer(@PathVariable Integer customerId, @PathVariable Integer productId, @RequestBody Review review) {
        Review savedReview = reviewService.addReviewForCustomer(customerId, productId, review);
        return new ResponseEntity<>(savedReview, HttpStatus.CREATED);
    }

    @GetMapping("/api/customers/{customerId}/products/{productId}/reviews/check")
    public ResponseEntity<Boolean> checkReviewExists(@PathVariable Integer customerId, @PathVariable Integer productId) {
        boolean reviewExists = reviewService.checkReviewExists(customerId, productId);
        return ResponseEntity.ok(reviewExists);
    }





//    @GetMapping("/api/customers/{customerId}/products/{productId}/check")
//    public ResponseEntity<Boolean> hasCustomerPurchasedProduct(@PathVariable Integer customerId, @PathVariable Integer productId) {
//        boolean hasPurchased = orderDetailService.hasCustomerPurchasedProduct(customerId, productId);
//        return ResponseEntity.ok(hasPurchased);
//    }

//    @PatchMapping("/{customerId}/reviews/{reviewId}")
//    public ResponseEntity<Review> patchReview(@PathVariable Integer customerId, @PathVariable Integer reviewId, @RequestBody Review reviewUpdates) {
//        Review patchedReview = reviewService.patchReview(customerId, reviewId, reviewUpdates);
//        if (patchedReview != null) {
//            return ResponseEntity.ok(patchedReview);
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }






}
