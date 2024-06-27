package com.example.RocketCart.repository;
import com.example.RocketCart.model.OrderDetail;
import com.example.RocketCart.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {
//    Optional<OrderDetail> getOrderDetailById(Integer id);

    List<OrderDetail> getOrderDetailsByOrderId(Integer orderId);

    List<OrderDetail> findByProduct(Product existingProduct);

    List<OrderDetail> findByProductIn(List<Product> products);


    @Query(value =
            "SELECT " +
                    "    EXTRACT(YEAR FROM ot.order_date) AS year, " +
                    "    EXTRACT(MONTH FROM ot.order_date) AS month, " +
                    "    SUM(od.quantity * p.price) AS total " +
                    "FROM " +
                    "    order_detail od " +
                    "JOIN " +
                    "    order_table ot ON od.order_id = ot.order_id " +
                    "JOIN " +
                    "    product p ON od.product_id = p.product_id " +
                    "WHERE " +
                    "    ot.order_date >= CURRENT_DATE - INTERVAL '6 months' " +
                    "GROUP BY " +
                    "    year, month " +
                    "ORDER BY " +
                    "    year ASC, month ASC",
            nativeQuery = true)
    List<Object[]> getProductValuesLast6Months();


    @Query(value =
            "SELECT " +
                    "    EXTRACT(YEAR FROM ot.order_date) AS year, " +
                    "    EXTRACT(MONTH FROM ot.order_date) AS month, " +
                    "    SUM(od.quantity * p.price) AS total " +
                    "FROM " +
                    "    order_detail od " +
                    "JOIN " +
                    "    order_table ot ON od.order_id = ot.order_id " +
                    "JOIN " +
                    "    product p ON od.product_id = p.product_id " +
                    "WHERE " +
                    "    ot.order_date >= CURRENT_DATE - INTERVAL '6 months' " +
                    "    AND p.seller_id = :sellerId " + // Filter by seller_id
                    "GROUP BY " +
                    "    year, month " +
                    "ORDER BY " +
                    "    year ASC, month ASC",
            nativeQuery = true)
    List<Object[]> getProductValuesLast6MonthsSeller(@Param("sellerId") Integer sellerId);


}
