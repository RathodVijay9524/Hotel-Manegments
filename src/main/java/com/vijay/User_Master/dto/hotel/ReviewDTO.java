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
public class ReviewDTO {
    private Long id;
    private Long menuItemId;
    private String menuItemName;
    private Long userId;
    private String userName;
    private Integer rating;
    private String comment;
    private Boolean isVerifiedPurchase;
    private Boolean isApproved;
    private Integer helpfulCount;
    private Boolean isEdited;
    private LocalDateTime editedAt;
    private LocalDateTime createdAt;
}
