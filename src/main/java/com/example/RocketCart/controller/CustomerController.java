package com.example.RocketCart.controller;

import com.example.RocketCart.model.Cart;
import com.example.RocketCart.model.Customer;
import com.example.RocketCart.repository.CartRepository;
import com.example.RocketCart.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class CustomerController {

    private final CustomerRepository customerRepository;
    private final CartRepository cartRepository;

    @Autowired
    public CustomerController(CustomerRepository customerRepository, CartRepository cartRepository) {
        this.customerRepository = customerRepository;
        this.cartRepository = cartRepository;
    }

    @PostMapping("/api/customers")
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {
        Customer newCustomer = customerRepository.save(customer);
        return new ResponseEntity<>(newCustomer, HttpStatus.CREATED);
    }

    @GetMapping("/api/customers/{customerId}")
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

}

