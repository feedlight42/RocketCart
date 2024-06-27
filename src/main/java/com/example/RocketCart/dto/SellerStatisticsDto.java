package com.example.RocketCart.dto;

import java.util.Map;

public class SellerStatisticsDto {
    private Double totalRevenue;
    private Map<String, Double> monthlyRevenue;

    public SellerStatisticsDto(Double totalRevenue, Map<String, Double> monthlyRevenue, Map<String, Double> monthlyCount) {
        this.totalRevenue = totalRevenue;
        this.monthlyRevenue = monthlyRevenue;
    }

    public Double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(Double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public Map<String, Double> getMonthlyRevenue() {
        return monthlyRevenue;
    }

    public void setMonthlyRevenue(Map<String, Double> monthlyRevenue) {
        this.monthlyRevenue = monthlyRevenue;
    }

    // Getters and setters
}


