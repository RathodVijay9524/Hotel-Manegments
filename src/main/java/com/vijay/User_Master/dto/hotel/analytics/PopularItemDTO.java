package com.vijay.User_Master.dto.hotel.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PopularItemDTO {
    private Long itemId;
    private String itemName;
    private String categoryName;
    private Long orderCount;
    private Long totalQuantitySold;
    private BigDecimal revenue;
    private Double averageRating;
}
