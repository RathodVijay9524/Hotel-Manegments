package com.vijay.User_Master.dto.hotel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryTrackingDTO {
    private Long id;
    private Long orderId;
    private String orderNumber;
    private Long agentId;
    private String agentName;
    private String agentPhone;
    private String status;
    private Double pickupLatitude;
    private Double pickupLongitude;
    private Double deliveryLatitude;
    private Double deliveryLongitude;
    private Double currentLatitude;
    private Double currentLongitude;
    private LocalDateTime assignedAt;
    private LocalDateTime pickedUpAt;
    private LocalDateTime deliveredAt;
    private Integer estimatedTimeMinutes;
    private Double distanceKm;
    private String deliveryNotes;
    private Double deliveryRating;
    private LocalDateTime createdAt;
}
