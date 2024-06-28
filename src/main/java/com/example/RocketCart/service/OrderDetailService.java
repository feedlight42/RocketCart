package com.example.RocketCart.service;

import com.example.RocketCart.model.Customer;
import com.example.RocketCart.model.OrderDetail;
import com.example.RocketCart.model.OrderTable;
import com.example.RocketCart.repository.CustomerRepository;
import com.example.RocketCart.repository.OrderDetailRepository;
import com.example.RocketCart.repository.OrderTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class OrderDetailService {

    private final OrderDetailRepository orderDetailRepository;
    private final CustomerRepository customerRepository;
    private final OrderTableRepository orderTableRepository;

    @Autowired
    public OrderDetailService(OrderDetailRepository orderDetailRepository, CustomerRepository customerRepository, OrderTableRepository orderTableRepository) {
        this.orderDetailRepository = orderDetailRepository;
        this.customerRepository = customerRepository;
        this.orderTableRepository = orderTableRepository;
    }

    public List<OrderDetail> findAll() {
        return orderDetailRepository.findAll();
    }

    public Optional<OrderDetail> findById(Integer orderDetailId) {
        return orderDetailRepository.findById(orderDetailId);
    }

    public List<OrderDetail> findByOrderId(Integer orderId) {
        return orderDetailRepository.findByOrderId(orderId);
    }

    public OrderDetail save(OrderDetail orderDetail) {
        return orderDetailRepository.save(orderDetail);
    }

    public List<OrderDetail> getOrderDetailsByOrderId(@PathVariable Integer orderId) {
        List<OrderDetail> orderDetails = orderDetailRepository.getOrderDetailsByOrderId(orderId);

        return orderDetails;
    }

    public void deleteById(Integer orderDetailId) {
        Optional<OrderDetail> orderDetailOptional = orderDetailRepository.findById(orderDetailId);
        if (orderDetailOptional.isPresent()) {
            OrderDetail orderDetail = orderDetailOptional.get();
            orderDetail.setDeleted(true);
            orderDetailRepository.save(orderDetail);
        }
    }


    public boolean hasCustomerPurchasedProduct(Integer customerId, Integer productId) {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null) {
            return false;
        }
        return orderDetailRepository.existsByCustomerIdAndProductId(customerId, productId);
    }
//    public boolean hasCustomerPurchasedProduct(Integer customerId, Integer productId) {
//        return orderDetailRepository.existsByCustomerIdAndProductId(customerId, productId);
//    }


}
