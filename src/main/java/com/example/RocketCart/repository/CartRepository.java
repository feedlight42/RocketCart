package com.example.RocketCart.repository;
import com.example.RocketCart.model.Cart;
import com.example.RocketCart.model.Customer;
import com.example.RocketCart.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Integer> {
    List<Cart> findAllByCustomerId(int customerId);

    void deleteAllByCustomerId(int customerId);

    List<Cart> findAllByCustomerIdAndDeletedFalse(Integer customerId);

    Optional<Cart> findByCartItemIdAndDeletedFalse(Integer cartItemId);

    List<Cart> findAllByDeletedFalse();

    Cart findByCustomerIdAndProduct(Integer customerId, Product product);
}
