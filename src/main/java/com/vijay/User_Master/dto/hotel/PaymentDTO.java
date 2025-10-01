package com.vijay.User_Master.dto.hotel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    private Long id;
    private Long orderId;
    private String orderNumber;
    private String transactionId;
    private BigDecimal amount;
    private String paymentMethod;
    private String status;
    private String paymentDetails;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;
}
