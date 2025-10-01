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
 * QRCode Entity - Represents QR codes for tables
 * Each QR code is linked to a specific business and table
 * When scanned, creates a guest session for contactless ordering
 */
@Entity
@Table(name = "qr_codes", indexes = {
    @Index(name = "idx_qr_business_id", columnList = "business_id"),
    @Index(name = "idx_qr_token", columnList = "qr_token")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class QRCode {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "business_id", nullable = false)
    private Long businessId; // Hotel/Restaurant owner ID
    
    @Column(name = "table_id", nullable = false)
    private Long tableId; // Restaurant table ID
    
    @Column(name = "qr_token", unique = true, nullable = false, length = 100)
    private String qrToken; // Unique token for security
    
    @Column(name = "qr_code_url", nullable = false, length = 500)
    private String qrCodeUrl; // Full URL that opens when scanned
    
    @Lob
    @Column(name = "qr_code_image", columnDefinition = "LONGTEXT")
    private String qrCodeImage; // Base64 encoded QR code image
    
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
    
    @Column(name = "scan_count")
    @Builder.Default
    private Integer scanCount = 0;
    
    @Column(name = "last_scanned_at")
    private LocalDateTime lastScannedAt;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Increment scan count when QR code is scanned
     */
    public void incrementScanCount() {
        this.scanCount++;
        this.lastScannedAt = LocalDateTime.now();
    }
}
