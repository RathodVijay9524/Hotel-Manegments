package com.vijay.User_Master.dto.hotel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentRequest {
    private Long orderId;
    private BigDecimal amount;
    private String paymentMethod; // CASH, CARD, UPI, WALLET, NET_BANKING
    private String paymentDetails; // Additional payment info as JSON
}
