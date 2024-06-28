package com.example.RocketCart.controller;

import com.example.RocketCart.dto.SellerStatisticsDto;
import com.example.RocketCart.model.*;
import com.example.RocketCart.repository.OrderDetailRepository;
import com.example.RocketCart.repository.OrderTableRepository;
import com.example.RocketCart.repository.ProductRepository;
import com.example.RocketCart.repository.SellerRepository;
import com.example.RocketCart.service.SellerStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/sellers")
public class SellerController {

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private OrderTableRepository orderTableRepository;

    @Autowired
    private SellerStatisticsService sellerStatisticsService;

    @GetMapping("/{sellerId}")
    public ResponseEntity<Seller> getSellerDetails(@PathVariable int sellerId) {
        Optional<Seller> seller = sellerRepository.findById(sellerId);
        return seller.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{sellerId}")
    public ResponseEntity<Void> updateSellerProfile(@PathVariable int sellerId, @RequestBody Seller sellerDetails) {
        if (!sellerRepository.existsById(sellerId)) {
            return ResponseEntity.notFound().build();
        }
        sellerDetails.setSellerId(sellerId); // Ensure the seller id is set correctly
        sellerRepository.save(sellerDetails);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{sellerId}/products")
    public ResponseEntity<Page<Product>> getProductsBySeller(
            @PathVariable int sellerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "", required = false) String searchKeyword) {

//        Pageable pageable = PageRequest.of(page, size, );
//        Pageable pageable = PageRequest.of(page, size, Sort.by("productId").ascending());
        Pageable pageable = PageRequest.of(page, size);
        Integer sort = 0;
        Sort.Direction sortDirection = (sort == 1) ? Sort.Direction.DESC : Sort.Direction.ASC;
        pageable = PageRequest.of(page, size, Sort.by(sortDirection, "productId"));
        Page<Product> products = productRepository.findBySellerIdAndProductNameContainingAndDeletedFalse(sellerId, searchKeyword, pageable);
        if (products.hasContent()) {
            return ResponseEntity.ok(products);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping("/{sellerId}/products")
    public ResponseEntity<Product> addProductForSale(@PathVariable int sellerId, @RequestBody Product product) {
        product.setSellerId(sellerId); // Set the seller id for the product
        productRepository.save(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }



//    YET TO TEST ------------


//    ERROR


//    returns number of products sod along with stock
    @GetMapping("/{sellerId}/products/{productId}/sold-statistics")
    public ResponseEntity<?> getproductssold(@PathVariable int sellerId, @PathVariable int productId) {

        Product existingProduct = productRepository.findByProductIdAndSellerIdAndDeletedFalse(productId, sellerId);

        if (existingProduct == null) {
            return ResponseEntity.notFound().build();
        }

        List<OrderDetail> orderDetails = orderDetailRepository.findByProduct(existingProduct);


        int totalQuantitySold = orderDetails.stream()
                .mapToInt(OrderDetail::getQuantity)
                .sum();

        double totalProductRevenue = totalQuantitySold * existingProduct.getPrice();

        Integer stock = existingProduct.getStock();

        List<Object> statisticsList = new ArrayList<>();
        statisticsList.add(totalQuantitySold);
        statisticsList.add(stock);

        return ResponseEntity.ok(statisticsList);

    }


    @GetMapping("/{sellerId}/count-stat")
    public List<Object[]> demo(@PathVariable Integer sellerId){
        return orderDetailRepository.getMonthlyProductSalesLast6Months(sellerId);
    }




//    !!!!!!!!!
    @GetMapping("/{sellerId}/stat")
    public ResponseEntity<Map<String, Object>> getSellerStat(@PathVariable Integer sellerId) {
        Map<String, Object> statistics = sellerStatisticsService.getSellerStatistics(sellerId);
        return ResponseEntity.ok(statistics);
    }


    @GetMapping("/{sellerId}/statistics")
    public ResponseEntity<SellerStatisticsDto> getSellerStatistics(@PathVariable Integer sellerId) {
        // Retrieve all products for the seller
        List<Product> products = productRepository.findProductBySellerId(sellerId);


        if (products.isEmpty()) {
            return ResponseEntity.notFound().build();
        }



        List<OrderDetail> orderDetails = orderDetailRepository.findByProductIn(products);

        // Retrieve all order IDs from the order details
        List<Integer> orderIds = orderDetails.stream()
                .map(OrderDetail::getOrderId)
                .collect(Collectors.toList());

        // Retrieve all orders by order IDs
        List<OrderTable> orders = orderTableRepository.findByOrderIdIn(orderIds);

        // Map order IDs to their corresponding order dates
        Map<Integer, LocalDate> orderDateMap = orders.stream()
                .collect(Collectors.toMap(
                        OrderTable::getOrderId,
                        order -> order.getOrderDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                ));

        // Initialize total revenue
        double totalRevenue = 0.0;

        // Calculate total revenue by iterating through order details
        for (OrderDetail orderDetail : orderDetails) {
            totalRevenue += orderDetail.getQuantity() * orderDetail.getProduct().getPrice();
        }

        // Calculate monthly revenue and count for the last 6 months
        LocalDate currentDate = LocalDate.now();
        LocalDate date6MonthsAgo = currentDate.minusMonths(6);

        Map<String, Double> monthlyRevenue = new TreeMap<>();
        Map<String, Double> monthlyCount = new TreeMap<>();

        // Initialize maps with the last 6 months
        for (int i = 0; i < 6; i++) {
            LocalDate month = currentDate.minusMonths(i);
            String monthKey = month.getYear() + "-" + String.format("%02d", month.getMonthValue());
            monthlyRevenue.put(monthKey, 0.0);
            monthlyCount.put(monthKey, 0.0);
        }

        // Populate maps with order details
        for (OrderDetail orderDetail : orderDetails) {
            LocalDate orderDate = orderDateMap.get(orderDetail.getOrderId());
            if (orderDate != null && !orderDate.isBefore(date6MonthsAgo)) {
                String monthKey = orderDate.getYear() + "-" + String.format("%02d", orderDate.getMonthValue());
                monthlyRevenue.put(monthKey, monthlyRevenue.get(monthKey) + (orderDetail.getQuantity() * orderDetail.getProduct().getPrice()));
                monthlyCount.put(monthKey, monthlyCount.get(monthKey) + 1);
            }
        }

        // Create statistics DTO
        SellerStatisticsDto statistics = new SellerStatisticsDto(totalRevenue, monthlyRevenue, monthlyCount);

        // Return the statistics
        return ResponseEntity.ok(statistics);
    }





//        // Retrieve all order details containing those products
//        List<OrderDetail> orderDetails = orderDetailRepository.findByProductIn(products);
//
//        // Initialize total amount
//        double totalRevenue = 0.0;
//
//        // Calculate total revenue by iterating through order details
//        for (OrderDetail orderDetail : orderDetails) {
//            totalRevenue += orderDetail.getQuantity() * orderDetail.getProduct().getPrice();
//        }
//
//        List<Integer> orderIds = orderDetails.stream()
//                .map(OrderDetail::getOrderId)
//                .collect(Collectors.toList());
//
//        // Retrieve all orders by order IDs
//        List<OrderTable> orders = orderTableRepository.findByOrderIdIn(orderIds);
//
//        // Map order IDs to their corresponding order dates
//        Map<Integer, LocalDate> orderDateMap = orders.stream()
//                .collect(Collectors.toMap(
//                        OrderTable::getOrderId,
//                        order -> order.getOrderDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
//                ));
//
//        LocalDate currentDate = LocalDate.now();
//        LocalDate date6MonthsAgo = currentDate.minusMonths(6);
//
//        Map<String, Double> monthlyRevenue = new TreeMap<>();
//        for (OrderDetail orderDetail : orderDetails) {
//            LocalDate orderDate = orderDateMap.get(orderDetail.getOrderId());
//            if (!orderDate.isBefore(date6MonthsAgo)) {
//                String monthKey = orderDate.getYear() + "-" + String.format("%02d", orderDate.getMonthValue());
//                monthlyRevenue.put(monthKey, monthlyRevenue.getOrDefault(monthKey, 0.0) + (orderDetail.getQuantity() * orderDetail.getProduct().getPrice()));
//            }
//        }



//        // Retrieve all order IDs from the order details
//        List<Integer> orderIds = orderDetails.stream()
//                .map(OrderDetail::getOrderId)
//                .collect(Collectors.toList());
//
//        // Retrieve all orders by order IDs
//        List<OrderTable> orders = orderTableRepository.findByOrderIdIn(orderIds);
//
//        // Calculate total revenue
//        Double totalRevenue = orders.stream()
//                .mapToDouble(OrderTable::getTotalAmount)
//                .sum();
//
//        // Calculate monthly revenue for the last 12 months
//        Map<String, Double> monthlyRevenue = orders.stream()
//                .filter(order -> {
//                    Calendar cal = Calendar.getInstance();
//                    cal.add(Calendar.MONTH, -12);
//                    return order.getOrderDate().after(cal.getTime());
//                })
//                .collect(Collectors.groupingBy(
//                        order -> new SimpleDateFormat("yyyy-MM").format(order.getOrderDate()),
//                        TreeMap::new,
//                        Collectors.summingDouble(OrderTable::getTotalAmount)
//                ));
//
//        // Create statistics DTO
//        SellerStatisticsDto statistics = new SellerStatisticsDto(totalRevenue, monthlyRevenue);
//
//        // Return the statistics
//        return ResponseEntity.ok(statistics);




//
    @PutMapping("/{sellerId}/products/{productId}")
    public ResponseEntity<Product> updateProduct(@PathVariable int sellerId, @PathVariable int productId, @RequestBody Product productDetails) {
        Product existingProduct = productRepository.findByProductIdAndSellerIdAndDeletedFalse(productId, sellerId);
        if (existingProduct == null) {
            return ResponseEntity.notFound().build();
        }
        productDetails.setProductId(productId);
        productDetails.setSellerId(sellerId);
        productRepository.save(productDetails);
        return ResponseEntity.ok(productDetails);
    }



    @DeleteMapping("/{sellerId}/products/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable int sellerId, @PathVariable int productId) {
        Product product = productRepository.findByProductIdAndSellerIdAndDeletedFalse(productId, sellerId);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        productRepository.delete(product);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("{sellerId}/products/{productId}/toggle-disable")
    public ResponseEntity<Product> toggleProductDisableState(@PathVariable Integer productId) {

        // Check if the product exists
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }

        // Toggle the isDisabled state
        product.setDisabled(!product.isDisabled());

        // Save the updated product
        Product updatedProduct = productRepository.save(product);

        // Return the updated product in the response
        return ResponseEntity.ok(updatedProduct);
    }

}

