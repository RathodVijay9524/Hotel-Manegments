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
@Table(name = "hotel_table_reservations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class TableReservation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 50)
    private String reservationNumber;
    
    @Column(nullable = false)
    private Long userId; // User who made the reservation
    
    @Column(nullable = false, length = 100)
    private String customerName;
    
    @Column(nullable = false, length = 15)
    private String customerPhone;
    
    @Column(length = 100)
    private String customerEmail;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "table_id")
    private RestaurantTable table;
    
    @Column(nullable = false)
    private Integer numberOfGuests;
    
    @Column(nullable = false)
    private LocalDateTime reservationDateTime;
    
    @Column(nullable = false)
    @Builder.Default
    private Integer durationMinutes = 120; // Default 2 hours
    
    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ReservationStatus status = ReservationStatus.PENDING;
    
    @Column(columnDefinition = "TEXT")
    private String specialRequests;
    
    @Column(length = 50)
    private String occasion; // Birthday, Anniversary, Business Meeting, etc.
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean requiresHighChair = false;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean requiresWheelchairAccess = false;
    
    private LocalDateTime checkedInAt;
    
    private LocalDateTime checkedOutAt;
    
    @Column(columnDefinition = "TEXT")
    private String cancellationReason;
    
    private LocalDateTime cancelledAt;
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    public enum ReservationStatus {
        PENDING,          // Reservation requested
        CONFIRMED,        // Reservation confirmed
        CHECKED_IN,       // Customer arrived and seated
        COMPLETED,        // Customer left
        CANCELLED,        // Reservation cancelled
        NO_SHOW          // Customer didn't show up
    }
}
