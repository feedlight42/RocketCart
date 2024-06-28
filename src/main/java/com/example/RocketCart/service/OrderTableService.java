package com.example.RocketCart.service;

import com.example.RocketCart.exceptions.InsufficientStockException;
import com.example.RocketCart.model.*;
import com.example.RocketCart.repository.CartRepository;
import com.example.RocketCart.repository.OrderDetailRepository;
import com.example.RocketCart.repository.OrderTableRepository;
import com.example.RocketCart.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class OrderTableService {

    private final OrderTableRepository orderTableRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;


    @Autowired
    public OrderTableService(OrderTableRepository orderTableRepository) {
        this.orderTableRepository = orderTableRepository;
    }

    public List<OrderTable> findAll() {
        return orderTableRepository.findAll();
    }

    public Optional<OrderTable> findById(Integer orderId) {
        return orderTableRepository.findById(orderId);
    }

    public List<OrderTable> findAllByCustomerId(Integer customerId) {
        return orderTableRepository.findAllByCustomerId(customerId);
    }



    public OrderTable save(OrderTable orderTable) {
        return orderTableRepository.save(orderTable);
    }

    public List<OrderTable> getOrderHistory(@PathVariable int customerId) {
        return orderTableRepository.findAllByCustomerIdOrderByOrderDateDesc(customerId);
    }

    public void deleteById(Integer orderId) {
        Optional<OrderTable> orderTableOptional = orderTableRepository.findById(orderId);
        if (orderTableOptional.isPresent()) {
            OrderTable orderTable = orderTableOptional.get();
            orderTable.setDeleted(true);
            orderTableRepository.save(orderTable);
        }
    }

    public void updatePaymentStatus(Integer orderId, String paymentStatus) {
        Optional<OrderTable> orderTableOptional = orderTableRepository.findById(orderId);
        if (orderTableOptional.isPresent()) {
            OrderTable orderTable = orderTableOptional.get();
            orderTable.setPaymentStatus(paymentStatus);
            orderTableRepository.save(orderTable);
        }
    }

    @Scheduled(fixedRate = 60000) // Runs every 60 seconds
    public void cancelUnpaidOrders() {
        System.out.println("_________________ REFRESHING ORDER DETAILS");
        Date currentTime = new Date();
        List<OrderTable> orders = orderTableRepository.findAll();
        for (OrderTable order : orders) {
            if ("pending".equalsIgnoreCase(order.getPaymentStatus())) {
                long timeDifference = currentTime.getTime() - order.getOrderDate().getTime();
                if (timeDifference > 5 * 60 * 1000) { // 5 minutes
                    order.setStatus("cancelled");
                    orderTableRepository.save(order);
                }
            }
        }
    }


    public OrderTable placeOrder(int customerId) throws InsufficientStockException {

        List<Cart> cartItems = cartRepository.findAllByCustomerIdAndDeletedFalse(customerId);
        System.out.println("inside place order method");

        double totalAmount = 0;

        for (Cart cartItem : cartItems) {
            System.out.println(cartItem.getCartItemId());
            System.out.println(cartItem.getQuantity());

            System.out.println("inside iter method");
            Product product = productRepository.findById(cartItem.getProduct().getProductId()).orElse(null);
            System.out.println(product.getStock());

            if (product != null) {
                totalAmount += (product.getPrice() * cartItem.getQuantity());
                int orderedQuantity = cartItem.getQuantity();

                if (product.getStock() >= orderedQuantity) {
                    product.setStock(product.getStock() - orderedQuantity);
                } else {
                    throw new InsufficientStockException("Insufficient stock for product: " + product.getProductName());
                }
                productRepository.save(product);
            }
        }

        OrderTable newOrder = new OrderTable();
        newOrder.setCustomerId(customerId);
        newOrder.setOrderDate(new Date());
        newOrder.setTotalAmount(totalAmount);
        newOrder.setStatus("Pending");
        System.out.println(newOrder);
        return orderTableRepository.save(newOrder);
    }




    public void makePayment(int customerId, Payment paymentRequest) {
        List<Cart> cartItems = cartRepository.findAllByCustomerIdAndDeletedFalse(customerId);

        // Retrieve the order that is pending for this customer
        OrderTable pendingOrder = orderTableRepository.findByCustomerIdAndPaymentStatus(customerId, "Pending").orElse(null);
        if (pendingOrder == null) {
            throw new IllegalStateException("No pending order found for the customer.");
        }

        Payment newPayment = new Payment();
        newPayment.setOrderTableId(pendingOrder.getOrderId());
        newPayment.setPaymentDate(new Date());
        newPayment.setPaymentMethod(paymentRequest.getPaymentMethod());
        newPayment.setAmount(pendingOrder.getTotalAmount());

//        paymentRepository.save(newPayment);

        for (Cart cartItem : cartItems) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(pendingOrder.getOrderId());
            orderDetail.setProduct(cartItem.getProduct());
            orderDetail.setQuantity(cartItem.getQuantity());

            orderDetailRepository.save(orderDetail);
        }

        // Clear the cart after moving items to order details
        cartRepository.deleteAllByCustomerId(customerId);

        // Update order status to done
        pendingOrder.setStatus("Done");
        orderTableRepository.save(pendingOrder);
    }

}
