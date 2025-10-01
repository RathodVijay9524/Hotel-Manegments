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
@Table(name = "hotel_delivery_tracking")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class DeliveryTracking {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id")
    private DeliveryAgent agent;
    
    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private DeliveryStatus status = DeliveryStatus.PENDING;
    
    private Double pickupLatitude;
    
    private Double pickupLongitude;
    
    private Double deliveryLatitude;
    
    private Double deliveryLongitude;
    
    private Double currentLatitude;
    
    private Double currentLongitude;
    
    private LocalDateTime assignedAt;
    
    private LocalDateTime pickedUpAt;
    
    private LocalDateTime deliveredAt;
    
    @Column(nullable = false)
    @Builder.Default
    private Integer estimatedTimeMinutes = 30;
    
    private Double distanceKm;
    
    @Column(columnDefinition = "TEXT")
    private String deliveryNotes;
    
    @Column(columnDefinition = "TEXT")
    private String customerFeedback;
    
    private Double deliveryRating;
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    public enum DeliveryStatus {
        PENDING,           // Waiting for agent assignment
        ASSIGNED,          // Agent assigned
        PICKED_UP,         // Food picked up from restaurant
        IN_TRANSIT,        // On the way to customer
        ARRIVED,           // Reached customer location
        DELIVERED,         // Successfully delivered
        CANCELLED,         // Delivery cancelled
        FAILED             // Delivery failed
    }
}
