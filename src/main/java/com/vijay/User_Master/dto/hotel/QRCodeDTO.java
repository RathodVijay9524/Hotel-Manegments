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
public class QRCodeDTO {
    private Long id;
    private Long businessId;
    private Long tableId;
    private String tableNumber;
    private String tableName;
    private String qrToken;
    private String qrCodeUrl;
    private String qrCodeImage; // Base64 encoded
    private Boolean isActive;
    private Integer scanCount;
    private LocalDateTime lastScannedAt;
    private LocalDateTime createdAt;
}
