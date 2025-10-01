package com.vijay.User_Master.entity.hotel;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "hotel_tables")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class RestaurantTable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 20)
    private String tableNumber;
    
    @Column(length = 100)
    private String tableName;
    
    @Column(nullable = false)
    private Integer capacity;
    
    @Column(length = 100)
    private String location; // Floor 1, Floor 2, Garden, etc.
    
    @Column(length = 500)
    private String qrCode; // QR code URL for table ordering
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean isAvailable = true;
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
