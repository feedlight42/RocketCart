package com.example.RocketCart.controller;

import com.example.RocketCart.model.Admin;
import com.example.RocketCart.model.Customer;
import com.example.RocketCart.model.Seller;
import com.example.RocketCart.repository.AdminRepository;
import com.example.RocketCart.repository.CustomerRepository;
import com.example.RocketCart.repository.SellerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AdminRepository adminRepository;


    @GetMapping("/{id}")
    public ResponseEntity<Admin> getAdminById(@PathVariable Integer id) {
        Optional<Admin> customer = adminRepository.findById(id);
        return customer.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }




    // Get a list of all customers
    @GetMapping("/customers")
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    // Get customer details by customer ID
    @GetMapping("/customers/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Integer id) {
        Optional<Customer> customer = customerRepository.findById(id);
        return customer.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }





    // Get a list of all sellers
    @GetMapping("/sellers")
    public List<Seller> getAllSellers() {
        return sellerRepository.findAll();
    }

    @GetMapping("/sellers/verified")
    public List<Seller> getVerifiedSellers() {
        return sellerRepository.findAllByVerifiedTrue();
    }


    // Get a list of all sellers
    @GetMapping("/sellers/not-verified")
    public List<Seller> getAllNotVerifiedSellers() {
        return sellerRepository.findAllByVerifiedFalse();
    }

    // Get seller details by seller ID
    @GetMapping("/sellers/{id}")
    public ResponseEntity<Seller> getSellerById(@PathVariable Integer id) {
        Optional<Seller> seller = sellerRepository.findById(id);
        return seller.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }




    @DeleteMapping("/customers/{id}")
    public ResponseEntity<Void> deleteCustomerById(@PathVariable Integer id) {
        Optional<Customer> customer = customerRepository.findById(id);
        if (customer.isPresent()) {
            customerRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }







    // Update seller details
//    @PutMapping("/sellers/{id}")
//    public ResponseEntity<Seller> updateSeller(@PathVariable Integer id, @RequestBody Seller sellerDetails) {
//        Optional<Seller> optionalSeller = sellerRepository.findById(id);
//        if (optionalSeller.isPresent()) {
//            Seller existingSeller = optionalSeller.get();
//            existingSeller.setName(sellerDetails.getName());
//            existingSeller.setEmail(sellerDetails.getEmail());
//            existingSeller.setPhone(sellerDetails.getPhone());
//            sellerRepository.save(existingSeller);
//            return ResponseEntity.ok(existingSeller);
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }








}
