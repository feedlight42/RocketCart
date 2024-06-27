package com.example.RocketCart.repository;
import com.example.RocketCart.model.OrderTable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderTableRepository extends JpaRepository<OrderTable, Integer> {
//    List<OrderTable> findAllByCustomerId(int customerId);

    List<OrderTable> findAllByCustomerIdOrderByOrderDateDesc(int customerId);

    List<OrderTable> findByOrderIdIn(List<Integer> orderIds);

    List<OrderTable> findByPaymentStatus(String notDone);

    List<OrderTable> findAllByCustomerId(int customerId);
}
