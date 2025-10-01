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
@Table(name = "hotel_delivery_agents")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class DeliveryAgent {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(nullable = false, unique = true, length = 15)
    private String phone;
    
    @Column(nullable = false, unique = true, length = 100)
    private String email;
    
    @Column(length = 50)
    private String vehicleType; // Bike, Scooter, Car
    
    @Column(length = 20)
    private String vehicleNumber;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean isAvailable = true;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean isOnline = false;
    
    private Double currentLatitude;
    
    private Double currentLongitude;
    
    @Builder.Default
    private Double rating = 0.0;
    
    @Column(nullable = false)
    @Builder.Default
    private Integer totalDeliveries = 0;
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
