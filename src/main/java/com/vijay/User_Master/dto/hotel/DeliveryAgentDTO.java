package com.vijay.User_Master.dto.hotel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryAgentDTO {
    private Long id;
    private String name;
    private String phone;
    private String email;
    private String vehicleType;
    private String vehicleNumber;
    private Boolean isAvailable;
    private Boolean isOnline;
    private Double currentLatitude;
    private Double currentLongitude;
    private Double rating;
    private Integer totalDeliveries;
}
