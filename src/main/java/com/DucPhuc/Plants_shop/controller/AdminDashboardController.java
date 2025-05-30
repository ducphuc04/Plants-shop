package com.DucPhuc.Plants_shop.controller;

import com.DucPhuc.Plants_shop.dto.response.ApiResponse;
import com.DucPhuc.Plants_shop.dto.response.BestSellerResponse;
import com.DucPhuc.Plants_shop.dto.response.DashboardSummaryResponse;
import com.DucPhuc.Plants_shop.dto.response.DailyRevenueResponse;
import com.DucPhuc.Plants_shop.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/dashboard")

public class AdminDashboardController {
    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/summary")
    public ApiResponse<DashboardSummaryResponse> getDashboardSummary() {
        DashboardSummaryResponse summary = dashboardService.getDashboardSummary();
        return ApiResponse.<DashboardSummaryResponse>builder()
                .result(summary)
                .build();
    }

    @GetMapping("/monthly-revenue")
    public ApiResponse<List<DailyRevenueResponse>> getMonthlyRevenue() {
        List<DailyRevenueResponse> monthlyRevenue = dashboardService.getMonthlyRevenue();
        return ApiResponse.<List<DailyRevenueResponse>>builder()
                .result(monthlyRevenue)
                .build();
    }

    @GetMapping("/best-sellers")
    public ApiResponse<List<BestSellerResponse>> getBestSellingProducts() {
        List<BestSellerResponse> bestSellers = dashboardService.getBestSellingProducts(10);
        return ApiResponse.<List<BestSellerResponse>>builder()
                .result(bestSellers)
                .build();
    }
    @GetMapping("/daily-revenue")
    public ApiResponse<List<DailyRevenueResponse>> getDailyRevenue() {
        List<DailyRevenueResponse> dailyRevenue = dashboardService.getDailyRevenue();
        return ApiResponse.<List<DailyRevenueResponse>>builder()
                .result(dailyRevenue)
                .build();
    }
}