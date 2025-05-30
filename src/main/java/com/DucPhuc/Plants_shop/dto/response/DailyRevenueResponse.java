package com.DucPhuc.Plants_shop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyRevenueResponse {
    private String date;
    private BigDecimal total;
    
    // For monthly revenue
    private Integer year;
    private Integer month;
    private Integer revenue;

    public DailyRevenueResponse(String date, BigDecimal total) {
        this.date = date;
        this.total = total;
    }
}