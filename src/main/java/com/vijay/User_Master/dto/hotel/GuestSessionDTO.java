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
public class GuestSessionDTO {
    private Long id;
    private String sessionToken;
    private Long businessId;
    private String businessName;
    private Long tableId;
    private String tableNumber;
    private String tableName;
    private String guestName;
    private String guestPhone;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
}
