package com.example.RocketCart.service;

import com.example.RocketCart.model.Seller;
import com.example.RocketCart.repository.SellerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SellerService {

    private final SellerRepository sellerRepository;

    @Autowired
    public SellerService(SellerRepository sellerRepository) {
        this.sellerRepository = sellerRepository;
    }

    public List<Seller> getAllSellers() {
        return sellerRepository.findAllByDeletedFalse();
    }

    public Optional<Seller> getSellerById(Integer sellerId) {
        return sellerRepository.findBySellerIdAndDeletedFalse(sellerId);
    }

    public Seller createSeller(Seller seller) {
        return sellerRepository.save(seller);
    }

    public Seller updateSeller(Integer sellerId, Seller updatedSeller) {
        Optional<Seller> sellerOptional = sellerRepository.findBySellerIdAndDeletedFalse(sellerId);
        if (sellerOptional.isPresent()) {
            updatedSeller.setSellerId(sellerId);
            return sellerRepository.save(updatedSeller);
        } else {
            return null;
        }
    }

    public void deleteSeller(Integer sellerId) {
        Optional<Seller> sellerOptional = sellerRepository.findBySellerIdAndDeletedFalse(sellerId);
        sellerOptional.ifPresent(seller -> sellerRepository.delete(seller));
    }

    public void softDeleteSeller(Integer sellerId) {
        Optional<Seller> sellerOptional = sellerRepository.findBySellerIdAndDeletedFalse(sellerId);
        if (sellerOptional.isPresent()) {
            Seller seller = sellerOptional.get();
            seller.setDeleted(true);
            sellerRepository.save(seller);
        }
    }

    public List<Seller> getAllActiveSellers() {
        return sellerRepository.findAllByDeletedFalse();
    }
}
