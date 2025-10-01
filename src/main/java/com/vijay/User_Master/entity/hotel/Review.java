package com.vijay.User_Master.entity.hotel;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "hotel_reviews")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Review {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_id", nullable = false)
    private MenuItem menuItem;
    
    @Column(nullable = false)
    private Long userId; // User who wrote the review
    
    @Column(nullable = false)
    private String userName; // Cache the user name
    
    @Column(nullable = false)
    private Integer rating; // 1 to 5 stars
    
    @Column(columnDefinition = "TEXT")
    private String comment;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean isVerifiedPurchase = false; // Did user actually order this item?
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean isApproved = true; // Admin can moderate reviews
    
    @Column(nullable = false)
    @Builder.Default
    private Integer helpfulCount = 0; // How many users found this helpful
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean isEdited = false;
    
    private LocalDateTime editedAt;
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
