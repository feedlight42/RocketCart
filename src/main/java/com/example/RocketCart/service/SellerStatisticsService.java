package com.example.RocketCart.service;

import com.example.RocketCart.repository.OrderDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SellerStatisticsService {
    @Autowired
    private OrderDetailRepository orderDetailRepository;

    public Map<String, Object> getSellerStatistics(Integer sellerId) {
        Map<String, Object> statistics = new HashMap<>();

        // Total Revenue
        Double totalRevenueAllTime = orderDetailRepository.getTotalRevenueAllTime(sellerId);
        Double totalRevenueLast6Months = orderDetailRepository.getTotalRevenueLast6Months(sellerId);
        Double totalRevenueLastMonth = orderDetailRepository.getTotalRevenueLastMonth(sellerId);

        // Total Products Sold
        Integer totalProductsSoldAllTime = orderDetailRepository.getTotalProductsSoldAllTime(sellerId);
        Integer totalProductsSoldLast6Months = orderDetailRepository.getTotalProductsSoldLast6Months(sellerId);
        Integer totalProductsSoldLastMonth = orderDetailRepository.getTotalProductsSoldLastMonth(sellerId);

        // Monthly Product Sales
        List<Object[]> monthlyProductSalesLast6Months = orderDetailRepository.getMonthlyProductSalesLast6Months(sellerId);

        // Monthly Revenue
        List<Object[]> monthlyRevenueLast6Months = orderDetailRepository.getMonthlyRevenueLast6Months(sellerId);

        statistics.put("totalRevenueAllTime", totalRevenueAllTime != null ? totalRevenueAllTime : 0);
        statistics.put("totalRevenueLast6Months", totalRevenueLast6Months != null ? totalRevenueLast6Months : 0);
        statistics.put("totalRevenueLastMonth", totalRevenueLastMonth != null ? totalRevenueLastMonth : 0);

        statistics.put("totalProductsSoldAllTime", totalProductsSoldAllTime != null ? totalProductsSoldAllTime : 0);
        statistics.put("totalProductsSoldLast6Months", totalProductsSoldLast6Months != null ? totalProductsSoldLast6Months : 0);
        statistics.put("totalProductsSoldLastMonth", totalProductsSoldLastMonth != null ? totalProductsSoldLastMonth : 0);

        statistics.put("monthlyProductSalesLast6Months", monthlyProductSalesLast6Months);
        statistics.put("monthlyRevenueLast6Months", monthlyRevenueLast6Months);

        return statistics;
    }
}
