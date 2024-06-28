package com.example.RocketCart.service;

import com.example.RocketCart.model.Customer;
import com.example.RocketCart.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Optional<Customer> getCustomerById(Integer customerId) {
        return customerRepository.findByCustomerIdAndDeletedFalse(customerId);
    }

    public Customer createCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    public Customer updateCustomer(Integer customerId, Customer updatedCustomer) {
        Optional<Customer> existingCustomerOptional = customerRepository.findByCustomerIdAndDeletedFalse(customerId);
        if (existingCustomerOptional.isPresent()) {
            Customer existingCustomer = existingCustomerOptional.get();
            existingCustomer.setUsername(updatedCustomer.getUsername());
            existingCustomer.setEmail(updatedCustomer.getEmail());
            existingCustomer.setPassword(updatedCustomer.getPassword());
            existingCustomer.setAddress(updatedCustomer.getAddress());
            existingCustomer.setPhoneNumber(updatedCustomer.getPhoneNumber());
            existingCustomer.setBillingAddress(updatedCustomer.getBillingAddress());
            return customerRepository.save(existingCustomer);
        } else {
            return null;
        }
    }

    public boolean uploadProfilePicture(Integer customerId, MultipartFile file) throws IOException {
        Optional<Customer> customerOptional = customerRepository.findByCustomerIdAndDeletedFalse(customerId);
        if (customerOptional.isPresent()) {
            Customer customer = customerOptional.get();
            customer.setImageData(file.getBytes());
//            return customerRepository.save(customer);
            return true;
        } else {
            return false;
        }
    }

    public byte[] getProfilePicture(Integer customerId) {
        Optional<Customer> customerOptional = customerRepository.findByCustomerIdAndDeletedFalse(customerId);
        if (customerOptional.isPresent()) {
            Customer customer = customerOptional.get();
            return customer.getImageData();
        } else {
            return null;
        }
    }

    public void softDeleteCustomer(Integer customerId) {
        Optional<Customer> customerOptional = customerRepository.findByCustomerIdAndDeletedFalse(customerId);
        if (customerOptional.isPresent()) {
            Customer customer = customerOptional.get();
            customer.setDeleted(true);
            customerRepository.save(customer);
        }
    }

    public List<Customer> getAllActiveCustomers() {
        return customerRepository.findAllByDeletedFalse();
    }


}
