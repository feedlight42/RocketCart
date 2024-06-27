package com.example.RocketCart.service;

import com.example.RocketCart.model.Admin;
import com.example.RocketCart.model.CustomUserDetails;
import com.example.RocketCart.model.Customer;
import com.example.RocketCart.model.Seller;
import com.example.RocketCart.repository.AdminRepository;
import com.example.RocketCart.repository.CustomerRepository;
import com.example.RocketCart.repository.SellerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Admin admin = adminRepository.findByUsername(username);
        if (admin != null) {
            return new CustomUserDetails(admin.getUsername(), admin.getPassword(),admin.getAdminId(),
                    AuthorityUtils.createAuthorityList("ROLE_ADMIN"));
        }


        Customer customer = customerRepository.findByUsername(username);
        if (customer != null) {
            return new CustomUserDetails(customer.getUsername(), customer.getPassword(),customer.getCustomerId(),
                    AuthorityUtils.createAuthorityList("ROLE_CUSTOMER"));
        }

        Seller seller = sellerRepository.findByUsername(username);
        if (seller != null) {
            return new CustomUserDetails(seller.getUsername(), seller.getPassword(),seller.getSellerId(),
                    AuthorityUtils.createAuthorityList("ROLE_SELLER"));
        }

        throw new UsernameNotFoundException("User not found with username: " + username);
    }
}


