package com.DucPhuc.Plants_shop.service;

import com.DucPhuc.Plants_shop.dto.response.BestSellerResponse;
import com.DucPhuc.Plants_shop.dto.response.DashboardSummaryResponse;
import com.DucPhuc.Plants_shop.dto.response.DailyRevenueResponse;
import com.DucPhuc.Plants_shop.repository.OrderDetailsRepository;
import com.DucPhuc.Plants_shop.repository.OrderRepository;
import com.DucPhuc.Plants_shop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private OrderDetailsRepository orderDetailsRepository;

    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public DashboardSummaryResponse getDashboardSummary() {

        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        long totalProducts = productRepository.count();

        Integer totalIncome = orderRepository.getTotalIncomeInPeriod(thirtyDaysAgo);
        if (totalIncome == null) totalIncome = 0;

        Integer avgOrderValue = orderRepository.getAverageOrderValueInPeriod(thirtyDaysAgo);
        if (avgOrderValue == null) avgOrderValue = 0;
        
        return DashboardSummaryResponse.builder()
                .totalProducts((int) totalProducts)
                .totalIncome(totalIncome)
                .averageOrderValue(avgOrderValue)
                .build();
    }
    
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public List<DailyRevenueResponse> getMonthlyRevenue() {
        // Get revenue for the last 6 months
        LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);
        return orderRepository.getMonthlyRevenue(sixMonthsAgo);
    }
    
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public List<BestSellerResponse> getBestSellingProducts(int limit) {
        return orderDetailsRepository.findBestSellingProducts(limit);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public List<DailyRevenueResponse> getDailyRevenue() {
        // Get revenue for the last 30 days
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        return orderRepository.getDailyRevenue(thirtyDaysAgo);
    }
}