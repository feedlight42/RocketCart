package com.example.RocketCart.controller;

import com.example.RocketCart.model.Customer;
import com.example.RocketCart.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;




import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private CustomerRepository customerRepository;


    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody Customer user) {


        // Encrypt the password before storing in the database
        user.setPassword(user.getPassword());
        user.setEmail(user.getEmail());
        customerRepository.save(user);

        return ResponseEntity.ok("User registered successfully");
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> signUpRequest) {

        String username = signUpRequest.get("username");
        String password = signUpRequest.get("password");

        if (username == null || password == null) {
            return ResponseEntity.badRequest().body("Username and password are required");
        }

        Customer user;
        user = customerRepository.findByUsername(username);

        if (user == null || !user.getPassword().equals(password)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }

        return ResponseEntity.ok(user);

    }


}
