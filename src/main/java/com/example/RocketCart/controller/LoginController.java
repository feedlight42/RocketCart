package com.example.RocketCart.controller;

import com.example.RocketCart.model.*;
import com.example.RocketCart.repository.AdminRepository;
import com.example.RocketCart.repository.CustomerRepository;
import com.example.RocketCart.repository.SellerRepository;
import com.example.RocketCart.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.GrantedAuthority;



import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class LoginController {


    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    public LoginController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Autowired
    private SellerRepository sellerRepository;


    @PostMapping("/signup/ad")
    public String signup(@RequestBody Admin user) {
        // Check if the username is already taken
//        if (userDetailsService.loadUserByUsername(user.getUsername()).isPresent()) {
//            return "Username is already taken";
//        }

        // Hash the password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Save the user to the database
        adminRepository.save(user);
        return "User registered successfully";
    }


    @PostMapping("/signup")
    public String signup(@RequestBody Customer user) {
        // Check if the username is already taken
//        if (userDetailsService.loadUserByUsername(user.getUsername()).isPresent()) {
//            return "Username is already taken";
//        }

        // Hash the password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // Set the default role to USER

        // Save the user to the database
        customerRepository.save(user);
        return "User registered successfully";
    }

//    @PostMapping("/signup")
//    public ResponseEntity<?> signUp(@RequestBody Customer user) {
//
//
//        // Encrypt the password before storing in the database
//        user.setPassword(user.getPassword());
//        user.setEmail(user.getEmail());
//        customerRepository.save(user);
//
//        return ResponseEntity.ok("User registered successfully");
//    }


//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody Map<String, String> signUpRequest) {
//
//        String username = signUpRequest.get("username");
//        String password = signUpRequest.get("password");
//
//        if (username == null || password == null) {
//            return ResponseEntity.badRequest().body("Username and password are required");
//        }
//
//        Customer user;
//        user = customerRepository.findByUsername(username);
//
//        if (user == null || !user.getPassword().equals(password)) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
//        }
//
//        return ResponseEntity.ok(user);
//
//    }


    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequest authRequest) throws Exception {
        try {
            authenticate(authRequest.getUsername(), authRequest.getPassword());
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Incorrect username or password", e);
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return ResponseEntity.ok(new AuthResponse(jwt, roles));
    }

    private void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

    @PostMapping("/sellerlogin")
    public ResponseEntity<?> sellerlogin(@RequestBody Map<String, String> signUpRequest) {

        String username = signUpRequest.get("username");
        String password = signUpRequest.get("password");

        if (username == null || password == null) {
            return ResponseEntity.badRequest().body("Username and password are required");
        }

        try {
            authenticate(username, password);
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Incorrect username or password", e);
        }

        Seller user;
        user = sellerRepository.findByUsername(username);

        if (user == null || !user.getPassword().equals(password)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }

        return ResponseEntity.ok(user);

    }





}
