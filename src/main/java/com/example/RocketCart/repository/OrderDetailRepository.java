package com.example.RocketCart.repository;
import com.example.RocketCart.model.Customer;
import com.example.RocketCart.model.OrderDetail;
import com.example.RocketCart.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {
//    Optional<OrderDetail> getOrderDetailById(Integer id);

    @Query(value =
            "SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END " +
                    "FROM order_detail od " +
                    "JOIN order_table ot ON od.order_id = ot.order_id " +
                    "WHERE ot.customer_id = :customerId " +
                    "AND od.product_id = :productId",
            nativeQuery = true)
    boolean existsByCustomerIdAndProductId(@Param("customerId") Integer customerId, @Param("productId") Integer productId);




    List<OrderDetail> getOrderDetailsByOrderId(Integer orderId);

    List<OrderDetail> findByProduct(Product existingProduct);

    List<OrderDetail> findByProductIn(List<Product> products);

//    @Query("SELECT COUNT(od) > 0 " +
//            "FROM OrderDetail od " +
//            "INNER JOIN OrderTable ot ON od.orderId = ot.orderId " +
//            "WHERE ot.customerId = :customerId AND od.productId = :productId")
//    boolean existsByCustomerIdAndProductId(Integer customerId, Integer productId);


//     seller statistics
    @Query(value =
        "SELECT " +
                "    SUM(od.quantity * p.price) AS total_revenue " +
                "FROM " +
                "    order_detail od " +
                "JOIN " +
                "    order_table ot ON od.order_id = ot.order_id " +
                "JOIN " +
                "    product p ON od.product_id = p.product_id " +
                "WHERE p.seller_id = :sellerId",
        nativeQuery = true)
    Double getTotalRevenueAllTime(@Param("sellerId") Integer sellerId);

    @Query(value =
            "SELECT " +
                    "    SUM(od.quantity * p.price) AS total_revenue " +
                    "FROM " +
                    "    order_detail od " +
                    "JOIN " +
                    "    order_table ot ON od.order_id = ot.order_id " +
                    "JOIN " +
                    "    product p ON od.product_id = p.product_id " +
                    "WHERE " +
                    "    ot.order_date >= CURRENT_DATE - INTERVAL '6 months' " +
                    "    AND p.seller_id = :sellerId",
            nativeQuery = true)
    Double getTotalRevenueLast6Months(@Param("sellerId") Integer sellerId);

    @Query(value =
            "SELECT " +
                    "    SUM(od.quantity * p.price) AS total_revenue " +
                    "FROM " +
                    "    order_detail od " +
                    "JOIN " +
                    "    order_table ot ON od.order_id = ot.order_id " +
                    "JOIN " +
                    "    product p ON od.product_id = p.product_id " +
                    "WHERE " +
                    "    ot.order_date >= CURRENT_DATE - INTERVAL '1 month' " +
                    "    AND p.seller_id = :sellerId",
            nativeQuery = true)
    Double getTotalRevenueLastMonth(@Param("sellerId") Integer sellerId);

    @Query(value =
            "SELECT " +
                    "    SUM(od.quantity) AS total_products_sold " +
                    "FROM " +
                    "    order_detail od " +
                    "JOIN " +
                    "    order_table ot ON od.order_id = ot.order_id " +
                    "JOIN " +
                    "    product p ON od.product_id = p.product_id " +
                    "WHERE p.seller_id = :sellerId",
            nativeQuery = true)
    Integer getTotalProductsSoldAllTime(@Param("sellerId") Integer sellerId);

    @Query(value =
            "SELECT " +
                    "    SUM(od.quantity) AS total_products_sold " +
                    "FROM " +
                    "    order_detail od " +
                    "JOIN " +
                    "    order_table ot ON od.order_id = ot.order_id " +
                    "JOIN " +
                    "    product p ON od.product_id = p.product_id " +
                    "WHERE " +
                    "    ot.order_date >= CURRENT_DATE - INTERVAL '6 months' " +
                    "    AND p.seller_id = :sellerId",
            nativeQuery = true)
    Integer getTotalProductsSoldLast6Months(@Param("sellerId") Integer sellerId);

    @Query(value =
            "SELECT " +
                    "    SUM(od.quantity) AS total_products_sold " +
                    "FROM " +
                    "    order_detail od " +
                    "JOIN " +
                    "    order_table ot ON od.order_id = ot.order_id " +
                    "JOIN " +
                    "    product p ON od.product_id = p.product_id " +
                    "WHERE " +
                    "    ot.order_date >= CURRENT_DATE - INTERVAL '1 month' " +
                    "    AND p.seller_id = :sellerId",
            nativeQuery = true)
    Integer getTotalProductsSoldLastMonth(@Param("sellerId") Integer sellerId);

    @Query(value =
            "WITH months AS (" +
                    "    SELECT generate_series(date_trunc('month', CURRENT_DATE - INTERVAL '5 months'), date_trunc('month', CURRENT_DATE), '1 month'::interval) AS month " +
                    "), sales AS (" +
                    "    SELECT " +
                    "        date_trunc('month', ot.order_date) AS month, " +
                    "        SUM(od.quantity) AS total_products_sold " +
                    "    FROM " +
                    "        order_detail od " +
                    "    JOIN " +
                    "        order_table ot ON od.order_id = ot.order_id " +
                    "    JOIN " +
                    "        product p ON od.product_id = p.product_id " +
                    "    WHERE " +
                    "        p.seller_id = :sellerId " +
                    "        AND ot.order_date >= CURRENT_DATE - INTERVAL '6 months' " +
                    "    GROUP BY " +
                    "        month " +
                    ") " +
                    "SELECT " +
                    "    EXTRACT(YEAR FROM m.month) AS year, " +
                    "    EXTRACT(MONTH FROM m.month) AS month, " +
                    "    COALESCE(s.total_products_sold, 0) AS total_products_sold " +
                    "FROM " +
                    "    months m " +
                    "LEFT JOIN " +
                    "    sales s ON m.month = s.month " +
                    "ORDER BY " +
                    "    year, month",
            nativeQuery = true)
    List<Object[]> getMonthlyProductSalesLast6Months(@Param("sellerId") Integer sellerId);

    @Query(value =
            "WITH months AS (" +
                    "    SELECT generate_series(date_trunc('month', CURRENT_DATE - INTERVAL '5 months'), date_trunc('month', CURRENT_DATE), '1 month'::interval) AS month " +
                    "), revenue AS (" +
                    "    SELECT " +
                    "        date_trunc('month', ot.order_date) AS month, " +
                    "        SUM(od.quantity * p.price) AS total_revenue " +
                    "    FROM " +
                    "        order_detail od " +
                    "    JOIN " +
                    "        order_table ot ON od.order_id = ot.order_id " +
                    "    JOIN " +
                    "        product p ON od.product_id = p.product_id " +
                    "    WHERE " +
                    "        p.seller_id = :sellerId " +
                    "        AND ot.order_date >= CURRENT_DATE - INTERVAL '6 months' " +
                    "    GROUP BY " +
                    "        month " +
                    ") " +
                    "SELECT " +
                    "    EXTRACT(YEAR FROM m.month) AS year, " +
                    "    EXTRACT(MONTH FROM m.month) AS month, " +
                    "    COALESCE(r.total_revenue, 0) AS total_revenue " +
                    "FROM " +
                    "    months m " +
                    "LEFT JOIN " +
                    "    revenue r ON m.month = r.month " +
                    "ORDER BY " +
                    "    year, month",
            nativeQuery = true)
    List<Object[]> getMonthlyRevenueLast6Months(@Param("sellerId") Integer sellerId);


    List<OrderDetail> findByOrderId(Integer orderId);

}






