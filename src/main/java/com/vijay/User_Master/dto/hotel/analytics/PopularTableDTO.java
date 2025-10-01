package com.vijay.User_Master.dto.hotel.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PopularTableDTO {
    private Long tableId;
    private String tableNumber;
    private String tableName;
    private String location;
    private Integer capacity;
    private Long bookingCount;
    private Double averageGuestsPerBooking;
}
