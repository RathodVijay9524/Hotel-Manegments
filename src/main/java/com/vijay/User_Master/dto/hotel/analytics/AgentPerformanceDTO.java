package com.vijay.User_Master.dto.hotel.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentPerformanceDTO {
    private Long agentId;
    private String agentName;
    private String phone;
    private String vehicleType;
    private Integer totalDeliveries;
    private Integer successfulDeliveries;
    private Integer failedDeliveries;
    private Double rating;
    private Double successRate;
    private Double averageDeliveryTimeMinutes;
}
