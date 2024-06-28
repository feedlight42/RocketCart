package com.example.RocketCart.service;

import com.example.RocketCart.model.Cart;
import com.example.RocketCart.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    private final CartRepository cartRepository;

    @Autowired
    public CartService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    public List<Cart> getCustomerCart(Integer customerId) {
        List<Cart> cartItems =  cartRepository.findAllByCustomerIdAndDeletedFalse(customerId);
        cartItems.sort(Comparator.comparingInt(Cart::getCartItemId));
//        cartItems.sort(Comparator.comparing(cart -> cart.getProduct().getProductName()));
        return  cartItems;
    }

    public Cart addItemToCart(Integer customerId, Cart cartItem) {
        Cart existingCartItem = cartRepository.findByCustomerIdAndProduct(customerId, cartItem.getProduct());

        if (existingCartItem != null) {
            // Product already exists in the cart, update the quantity
            existingCartItem.setQuantity(existingCartItem.getQuantity() + cartItem.getQuantity());
            return cartRepository.save(existingCartItem);
        } else {
            // Product does not exist in the cart, proceed to add new cart item
            cartItem.setCustomerId(customerId);
            return cartRepository.save(cartItem);
        }
    }

    public void deleteCartItem(Integer cartItemId) {
        Optional<Cart> cartOptional = cartRepository.findByCartItemIdAndDeletedFalse(cartItemId);

        cartOptional.ifPresent(cartRepository::delete);
    }

    public Cart updateCartItem(Integer customerId, Integer cartId, Cart updatedCart) {
        Optional<Cart> cartOptional = cartRepository.findByCartItemIdAndDeletedFalse(cartId);
        if (cartOptional.isPresent()) {
            updatedCart.setCartItemId(cartId);
            return cartRepository.save(updatedCart);
        } else {
            return null;
        }
    }

    public Cart updateCartItemQuantity(Integer customerId, Integer cartId, Object quantity) {
        Optional<Cart> cartOptional = cartRepository.findByCartItemIdAndDeletedFalse(cartId);
        if (cartOptional.isPresent()) {
            Cart existingCart = cartOptional.get();
            existingCart.setQuantity((Integer) quantity);
            return cartRepository.save(existingCart);
        } else {
            return null;
        }
    }

    public boolean softDeleteCartItem(Integer cartItemId) {
        Optional<Cart> cartOptional = cartRepository.findByCartItemIdAndDeletedFalse(cartItemId);

        return cartOptional.map(cart -> {
            cart.setDeleted(true); // Soft delete by setting the deleted flag to true
            cartRepository.save(cart);
            return true; // Return true indicating successful soft deletion
        }).orElse(false); // Return false if cart item with given ID and not deleted is not found
    }

    public List<Cart> getAllActiveCartItems() {
        return cartRepository.findAllByDeletedFalse();
    }

    public List<Cart> getCartItemsByCustomerId(Integer customerId) {
        return cartRepository.findAllByCustomerIdAndDeletedFalse(customerId);
    }
}
