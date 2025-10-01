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
public class CreateReservationRequest {
    private Long userId;
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private Long tableId;
    private Integer numberOfGuests;
    private LocalDateTime reservationDateTime;
    private Integer durationMinutes; // Optional, default 120
    private String specialRequests;
    private String occasion;
    private Boolean requiresHighChair;
    private Boolean requiresWheelchairAccess;
}
