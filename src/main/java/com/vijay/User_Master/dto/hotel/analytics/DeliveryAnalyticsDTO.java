package com.vijay.User_Master.dto.hotel.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryAnalyticsDTO {
    // Delivery metrics
    private Long totalDeliveries;
    private Long successfulDeliveries;
    private Long failedDeliveries;
    private Long activeDeliveries;
    
    // Performance metrics
    private Double averageDeliveryTimeMinutes;
    private Double successRate;
    private Double averageRating;
    
    // Agent metrics
    private Long totalAgents;
    private Long onlineAgents;
    private Long availableAgents;
    private Long busyAgents;
    
    // Top performing agents
    private List<AgentPerformanceDTO> topAgents;
    
    // Delivery status distribution
    private Long pendingDeliveries;
    private Long assignedDeliveries;
    private Long pickedUpDeliveries;
    private Long inTransitDeliveries;
    private Long deliveredToday;
}
