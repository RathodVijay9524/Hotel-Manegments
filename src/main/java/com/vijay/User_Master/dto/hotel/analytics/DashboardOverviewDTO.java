package com.vijay.User_Master.dto.hotel.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardOverviewDTO {
    // Today's metrics
    private Long todayOrders;
    private BigDecimal todayRevenue;
    private Long todayReservations;
    private Long activeDeliveries;
    
    // Overall metrics
    private Long totalOrders;
    private BigDecimal totalRevenue;
    private Long totalCustomers;
    private Long totalReservations;
    
    // Growth metrics (compared to yesterday/last period)
    private Double orderGrowthPercent;
    private Double revenueGrowthPercent;
    private Double reservationGrowthPercent;
    
    // Current status
    private Long onlineDeliveryAgents;
    private Long occupiedTables;
    private Long availableTables;
}
