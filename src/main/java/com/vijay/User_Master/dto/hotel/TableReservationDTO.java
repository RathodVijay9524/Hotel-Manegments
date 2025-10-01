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
public class TableReservationDTO {
    private Long id;
    private String reservationNumber;
    private Long userId;
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private Long tableId;
    private String tableNumber;
    private String tableName;
    private Integer numberOfGuests;
    private LocalDateTime reservationDateTime;
    private Integer durationMinutes;
    private String status;
    private String specialRequests;
    private String occasion;
    private Boolean requiresHighChair;
    private Boolean requiresWheelchairAccess;
    private LocalDateTime checkedInAt;
    private LocalDateTime checkedOutAt;
    private LocalDateTime createdAt;
}
