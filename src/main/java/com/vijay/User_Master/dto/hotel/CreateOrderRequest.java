package com.vijay.User_Master.dto.hotel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {
    private Long userId;
    private String customerName;
    private String customerPhone;
    private Long tableId;
    private String orderType; // DINE_IN, TAKEAWAY, DELIVERY, ROOM_SERVICE
    private List<OrderItemRequest> items;
    private String specialInstructions;
    private String deliveryAddress;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemRequest {
        private Long menuItemId;
        private Integer quantity;
        private String specialInstructions;
    }
}
