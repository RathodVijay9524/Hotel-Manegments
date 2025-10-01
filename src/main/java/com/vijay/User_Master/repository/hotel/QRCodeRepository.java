package com.vijay.User_Master.repository.hotel;

import com.vijay.User_Master.entity.hotel.QRCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QRCodeRepository extends JpaRepository<QRCode, Long> {
    
    // Find by QR token (for scanning)
    Optional<QRCode> findByQrToken(String qrToken);
    
    // Find by business ID
    List<QRCode> findByBusinessId(Long businessId);
    
    List<QRCode> findByBusinessIdAndIsActiveTrue(Long businessId);
    
    // Find by business and table
    Optional<QRCode> findByBusinessIdAndTableId(Long businessId, Long tableId);
    
    // Check if QR exists for table
    boolean existsByBusinessIdAndTableId(Long businessId, Long tableId);
    
    // Count QR codes for business
    Long countByBusinessId(Long businessId);
    
    Long countByBusinessIdAndIsActiveTrue(Long businessId);
    
    // Get QR codes by table
    List<QRCode> findByTableId(Long tableId);
    
    // Get most scanned QR codes
    @Query("SELECT q FROM QRCode q WHERE q.businessId = :businessId ORDER BY q.scanCount DESC")
    List<QRCode> findMostScannedByBusinessId(@Param("businessId") Long businessId);
}
