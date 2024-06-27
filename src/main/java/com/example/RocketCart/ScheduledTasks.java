package com.example.RocketCart;

import jakarta.persistence.criteria.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.RocketCart.repository.OrderTableRepository;
import com.example.RocketCart.model.OrderTable;

import java.util.List;

@Component
public class ScheduledTasks {

    @Autowired
    private OrderTableRepository orderRepository;

    // Runs once a day (86400000 milliseconds = 24 hours)
    @Scheduled(fixedDelay = 86400000)
    public void deleteUnpaidOrders() {
        List<OrderTable> unpaidOrders = orderRepository.findByPaymentStatus("not done");
        for (OrderTable order : unpaidOrders) {
            // Delete the order or handle accordingly
            orderRepository.delete(order);
        }
    }
}




