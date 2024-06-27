package com.example.RocketCart.service;

import com.example.RocketCart.model.OrderTable;
import com.example.RocketCart.repository.OrderTableRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderTableRepository orderTableRepository;

    @Scheduled(fixedDelay = 86400000) // Runs once a day (adjust as needed)
    public void deleteUnpaidOrders() {
        List<OrderTable> unpaidOrders = orderTableRepository.findByPaymentStatus("NOT_DONE");
        for (OrderTable order : unpaidOrders) {
            // Delete the order or handle accordingly
            orderTableRepository.delete(order);
        }
    }

    @Transactional
    public boolean processPayment(Integer orderId, String paymentMethod) {
        Optional<OrderTable> optionalOrder = orderTableRepository.findById(orderId);
        if (optionalOrder.isPresent()) {
            OrderTable order = optionalOrder.get();
            if (Objects.equals(order.getPaymentStatus(), "NOT_DONE")) {
                // Process payment logic here, update payment status to "done"
                order.setPaymentStatus("DONE");
                orderTableRepository.save(order);
                return true;
            }
        }
        return false;
    }
}
