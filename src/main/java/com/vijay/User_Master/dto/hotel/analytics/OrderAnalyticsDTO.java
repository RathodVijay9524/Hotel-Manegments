package com.vijay.User_Master.dto.hotel.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderAnalyticsDTO {
    // Summary metrics
    private Long totalOrders;
    private BigDecimal totalRevenue;
    private BigDecimal averageOrderValue;
    private Long pendingOrders;
    private Long completedOrders;
    
    // Order type distribution
    private Long dineInOrders;
    private Long takeawayOrders;
    private Long deliveryOrders;
    
    // Revenue by payment method
    private Map<String, BigDecimal> revenueByPaymentMethod;
    
    // Peak hours (hour -> order count)
    private Map<Integer, Long> ordersByHour;
    
    // Top selling items
    private List<PopularItemDTO> topSellingItems;
    
    // Daily revenue trend (last 7 days)
    private List<DailyRevenueDTO> dailyRevenueTrend;
}
