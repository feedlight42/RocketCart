package com.example.RocketCart.repository;
import com.example.RocketCart.model.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {
//    Optional<OrderDetail> getOrderDetailById(Integer id);

    List<OrderDetail> getOrderDetailsByOrderId(Integer orderId);
}
