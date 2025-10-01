package com.vijay.User_Master.entity.hotel;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * GuestSession Entity - Represents a guest's ordering session
 * Created when a guest scans a QR code
 * Inherits business_id and table_id from the QR code
 * Allows guests to order without authentication
 */
@Entity
@Table(name = "guest_sessions", indexes = {
    @Index(name = "idx_guest_business_id", columnList = "business_id"),
    @Index(name = "idx_guest_token", columnList = "session_token"),
    @Index(name = "idx_guest_table", columnList = "table_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class GuestSession {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "business_id", nullable = false)
    private Long businessId; // Inherited from QR code - ensures isolation
    
    @Column(name = "table_id", nullable = false)
    private Long tableId; // Inherited from QR code
    
    @Column(name = "session_token", unique = true, nullable = false, length = 100)
    private String sessionToken; // Used for guest authentication
    
    @Column(name = "guest_name", length = 100)
    private String guestName;
    
    @Column(name = "guest_phone", length = 20)
    private String guestPhone;
    
    @Column(name = "guest_email", length = 100)
    private String guestEmail;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private SessionStatus status = SessionStatus.ACTIVE;
    
    @Column(name = "device_info", length = 500)
    private String deviceInfo; // User agent, IP, etc.
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    /**
     * Session status
     */
    public enum SessionStatus {
        ACTIVE,      // Session is active and can place orders
        COMPLETED,   // Guest has finished and paid
        EXPIRED,     // Session has expired (timeout)
        CANCELLED    // Session was cancelled
    }
    
    /**
     * Check if session is valid (active and not expired)
     */
    public boolean isValid() {
        return this.status == SessionStatus.ACTIVE && 
               this.expiresAt.isAfter(LocalDateTime.now());
    }
    
    /**
     * Mark session as completed
     */
    public void complete() {
        this.status = SessionStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }
    
    /**
     * Check if session has expired
     */
    public boolean hasExpired() {
        return this.expiresAt.isBefore(LocalDateTime.now());
    }
}
