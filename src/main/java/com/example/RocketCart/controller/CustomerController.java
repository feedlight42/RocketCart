package com.example.RocketCart.controller;

import com.example.RocketCart.controller.exceptions.InsufficientStockException;
import com.example.RocketCart.model.*;
import com.example.RocketCart.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class CustomerController {

    private final CustomerRepository customerRepository;
    private final CartRepository cartRepository;
    private final OrderTableRepository orderTableRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final PaymentRepository paymentRepository;
    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;

    @Autowired
    public CustomerController(CustomerRepository customerRepository, CartRepository cartRepository, OrderTableRepository orderRepository, OrderDetailRepository orderDetailRepository, PaymentRepository paymentRepository, ProductRepository productRepository, ReviewRepository reviewRepository) {
        this.customerRepository = customerRepository;
        this.cartRepository = cartRepository;
        this.orderTableRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.paymentRepository = paymentRepository;
        this.productRepository = productRepository;

        this.reviewRepository = reviewRepository;
    }


    @PostMapping("/{customerId}/uploadProfilePicture")
    public ResponseEntity<?> uploadProfilePicture(@PathVariable Integer customerId, @RequestParam("file") MultipartFile file) {
        try {
            Customer customer = customerRepository.findByCustomerId(customerId);
            if (customer == null) {
                return ResponseEntity.notFound().build();
            }

            customer.setImageData(file.getBytes());
            customerRepository.save(customer);

            return ResponseEntity.ok().body("Profile picture uploaded successfully for customer ID: " + customerId);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload profile picture");
        }
    }

    @GetMapping("/{customerId}/profilePicture")
    public ResponseEntity<byte[]> getProfilePicture(@PathVariable Integer customerId) {
        Customer customer = customerRepository.findByCustomerId(customerId);
        if (customer != null && customer.getImageData() != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG) // Adjust content type based on your image format
                    .body(customer.getImageData());
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping("/api/customers")
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {
        Customer newCustomer = customerRepository.save(customer);
        return new ResponseEntity<>(newCustomer, HttpStatus.CREATED);
    }

    @GetMapping("/api/login/{customerId}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Integer customerId) {
        Optional<Customer> customerOptional = customerRepository.findById(customerId);
        return customerOptional.map(customer -> new ResponseEntity<>(customer, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/api/customers/{customerId}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Integer customerId, @RequestBody Customer updatedCustomer) {
        Optional<Customer> existingCustomerOptional = customerRepository.findById(customerId);
        if (existingCustomerOptional.isPresent()) {
            Customer existingCustomer = existingCustomerOptional.get();
            existingCustomer.setUsername(updatedCustomer.getUsername());
            existingCustomer.setEmail(updatedCustomer.getEmail());
            existingCustomer.setPassword(updatedCustomer.getPassword());
            existingCustomer.setAddress(updatedCustomer.getAddress());
            existingCustomer.setPhoneNumber(updatedCustomer.getPhoneNumber());
            existingCustomer.setBillingAddress(updatedCustomer.getBillingAddress());

            Customer updatedCustomerEntity = customerRepository.save(existingCustomer);
            return new ResponseEntity<>(updatedCustomerEntity, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }



    @GetMapping("/api/customers/{customerId}/cart")
    public ResponseEntity<List<Cart>> getCustomerCart(@PathVariable Integer customerId) {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer != null) {
            List<Cart> cartItems = customer.getCartItems();
            return new ResponseEntity<>(cartItems, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/api/customers/{customerId}/cart")
    public ResponseEntity<Cart> addItemToCart(@PathVariable Integer customerId, @RequestBody Cart cartItem) {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer != null) {
            cartItem.setCustomerId(customer.getCustomerId());
            Cart newCartItem = cartRepository.save(cartItem);
            return new ResponseEntity<>(newCartItem, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/api/customers/{customerId}/cart/{cartItemId}")
    public ResponseEntity<String> deleteCartItem(@PathVariable Integer cartItemId) {
        // Check if the cart item exists
        if (cartRepository.existsById(cartItemId)) {
            cartRepository.deleteById(cartItemId);
            return new ResponseEntity<>("Cart item deleted successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Cart item not found", HttpStatus.NOT_FOUND);
        }
    }


    @PutMapping("/api/customers/{customerId}/cart/{cartId}")
    public ResponseEntity<Cart> updateCartItem(@PathVariable Long customerId, @PathVariable Integer cartId, @RequestBody Cart updatedCart) {
        // Check if the cart item exists
        if (cartRepository.existsById(cartId)) {
            Cart existingCart = cartRepository.findById(cartId).orElse(null);
                updatedCart.setCartItemId(cartId);
                Cart updatedCartItem = cartRepository.save(updatedCart);
                return new ResponseEntity<>(updatedCartItem, HttpStatus.OK);

        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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
    public List<OrderTable> getOrderHistory(@PathVariable int customerId) {
        return orderTableRepository.findAllByCustomerIdOrderByOrderDateDesc(customerId);
    }

    @GetMapping("/api/customers/{customerId}/orderhistory/{orderId}")
    public ResponseEntity<List<OrderDetail>> getOrderDetailsByOrderId(@PathVariable Integer orderId) {
        List<OrderDetail> orderDetails = orderDetailRepository.getOrderDetailsByOrderId(orderId);

        if (!orderDetails.isEmpty()) {
            return ResponseEntity.ok(orderDetails);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @Transactional
    @PostMapping("/api/customers/{customerId}/payment")
    public void placeOrderAndMakePayment(@PathVariable int customerId, @RequestBody Payment paymentRequest) throws InsufficientStockException {
        // Logic to create new order
        List<Cart> cartItems = cartRepository.findAllByCustomerId(customerId);
        double totalAmount = 0;

        for (Cart cartItem : cartItems) {
            Product product = productRepository.findById(cartItem.getProduct().getProductId()).orElse(null);

            if (product != null) {
                totalAmount += (product.getPrice() * cartItem.getQuantity());
                int orderedQuantity = cartItem.getQuantity();

                // Update product stock
                if (product.getStock() >= orderedQuantity) {
                    product.setStock(product.getStock() - orderedQuantity);
                } else {
                    // Handle insufficient stock scenario
                    // You can throw an exception or handle it based on your requirements
                    // For example:
                    throw new InsufficientStockException("Insufficient stock for product: " + product.getProductName());
                }
                productRepository.save(product);
            }
        }

        // Create new order
        OrderTable newOrder = new OrderTable();
        newOrder.setCustomerId(customerId);
        newOrder.setOrderDate(new Date());
        newOrder.setTotalAmount(totalAmount);
        newOrder.setStatus("Pending");

        OrderTable savedOrder = orderTableRepository.save(newOrder);

        Payment newPayment = new Payment();
        newPayment.setOrderTableId(savedOrder.getOrderId());
        newPayment.setPaymentDate(new Date());
        newPayment.setPaymentMethod(paymentRequest.getPaymentMethod());
        newPayment.setAmount(totalAmount);

        // Save the payment
        Payment savedPayment = paymentRepository.save(newPayment);

        // Move cart items to order details and clear the cart
        for (Cart cartItem : cartItems) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(savedOrder.getOrderId());
            orderDetail.setProduct(cartItem.getProduct());
            orderDetail.setQuantity(cartItem.getQuantity());

            orderDetailRepository.save(orderDetail);
        }

        // Clear the cart after moving items to order details
        cartRepository.deleteAllByCustomerId(customerId);
    }






//    IMPLEMENTING CUSTOMER SIDE REVIEW ENDPOINTS

    // Endpoint to get all reviews of a customer
    @GetMapping("api/customers/{customerId}/reviews")
    public ResponseEntity<List<Review>> getAllReviewsForCustomer(@PathVariable Integer customerId) {
        Optional<Customer> customerOptional = customerRepository.findById(customerId);

        if (customerOptional.isPresent()) {
            List<Review> reviews = reviewRepository.findByCustomer(customerOptional.get());
            return new ResponseEntity<>(reviews, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @PostMapping("api/customers/{customerId}/products/{productId}/reviews")
    public ResponseEntity<Review> addReviewForCustomer(@PathVariable Integer customerId, @PathVariable Integer productId, @RequestBody Review review) {
        Optional<Customer> customerOptional = customerRepository.findById(customerId);
        Optional<Product> productOptional = productRepository.findById(productId);

        if (customerOptional.isPresent() && productOptional.isPresent()) {
            review.setCustomer(customerOptional.get());
            review.setProductId(productId);

            Review savedReview = reviewRepository.save(review);
            return new ResponseEntity<>(savedReview, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    // Endpoint to check if a review exists for a particular product ID by that customer
    @GetMapping("api/customers/{customerId}/products/{productId}/reviews/check")
    public ResponseEntity<Boolean> checkReviewExists(@PathVariable Integer customerId, @PathVariable Integer productId) {
        Optional<Customer> customerOptional = customerRepository.findById(customerId);
        Optional<Product> productOptional = productRepository.findById(productId);

        if (customerOptional.isPresent() && productOptional.isPresent()) {
            boolean exists = reviewRepository.existsByCustomerAndProductId(customerOptional.get(), productId);
            return new ResponseEntity<>(exists, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    // Endpoint to patch review content and rating
    @PatchMapping("/{customerId}/reviews/{reviewId}")
    public ResponseEntity<Review> patchReview(@PathVariable Integer customerId, @PathVariable Integer reviewId, @RequestBody Review reviewUpdates) {
        Optional<Customer> customerOptional = customerRepository.findById(customerId);
        Optional<Review> reviewOptional = reviewRepository.findById(reviewId);

        if (customerOptional.isPresent() && reviewOptional.isPresent()) {
            Review existingReview = reviewOptional.get();

            // Check if the review belongs to the customer
            if (!existingReview.getCustomer().getCustomerId().equals(customerId)) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

            // Update the review content and rating
            if (reviewUpdates.getRating() != null) {
                existingReview.setRating(reviewUpdates.getRating());
            }
            if (reviewUpdates.getComment() != null) {
                existingReview.setComment(reviewUpdates.getComment());
            }

            Review updatedReview = reviewRepository.save(existingReview);
            return new ResponseEntity<>(updatedReview, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}

