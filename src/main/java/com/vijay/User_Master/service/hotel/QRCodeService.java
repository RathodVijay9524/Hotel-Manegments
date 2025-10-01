package com.vijay.User_Master.service.hotel;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.vijay.User_Master.Helper.BusinessContextFilter;
import com.vijay.User_Master.dto.hotel.QRCodeDTO;
import com.vijay.User_Master.entity.hotel.QRCode;
import com.vijay.User_Master.entity.hotel.RestaurantTable;
import com.vijay.User_Master.repository.hotel.QRCodeRepository;
import com.vijay.User_Master.repository.hotel.RestaurantTableRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * QRCodeService - Manages QR code generation and management for tables
 * Each QR code is linked to a specific business and table for contactless ordering
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class QRCodeService {
    
    private final QRCodeRepository qrCodeRepository;
    private final RestaurantTableRepository tableRepository;
    private final BusinessContextFilter businessContext;
    
    @Value("${app.qr.base-url:https://yourhotel.com}")
    private String baseUrl;
    
    /**
     * Generate QR code for a table
     * If QR code already exists for the table, returns existing one
     */
    @Transactional
    public QRCodeDTO generateQRCodeForTable(Long tableId) {
        log.info("Generating QR code for table: {}", tableId);
        
        Long businessId = businessContext.getCurrentBusinessId();
        if (businessId == null) {
            throw new RuntimeException("Cannot generate QR code without business context");
        }
        
        // Verify table belongs to this business
        RestaurantTable table = tableRepository.findById(tableId)
                .orElseThrow(() -> new RuntimeException("Table not found"));
        
        if (table.getBusinessId() != null && !table.getBusinessId().equals(businessId)) {
            throw new RuntimeException("Access denied - Table belongs to different business");
        }
        
        // Check if QR code already exists for this table
        Optional<QRCode> existing = qrCodeRepository.findByBusinessIdAndTableId(businessId, tableId);
        if (existing.isPresent()) {
            log.info("QR code already exists for table: {}", tableId);
            return mapToDTO(existing.get());
        }
        
        // Generate unique token
        String qrToken = generateUniqueToken();
        
        // Create QR code URL
        String qrUrl = String.format("%s/menu?token=%s", baseUrl, qrToken);
        
        // Generate QR code image (Base64)
        String qrImage;
        try {
            qrImage = generateQRCodeImageBase64(qrUrl, 300, 300);
        } catch (Exception e) {
            log.error("Failed to generate QR code image", e);
            throw new RuntimeException("Failed to generate QR code image: " + e.getMessage());
        }
        
        // Create QR code entity
        QRCode qrCode = QRCode.builder()
                .businessId(businessId)
                .tableId(tableId)
                .qrToken(qrToken)
                .qrCodeUrl(qrUrl)
                .qrCodeImage(qrImage)
                .isActive(true)
                .scanCount(0)
                .build();
        
        qrCode = qrCodeRepository.save(qrCode);
        
        log.info("QR code generated successfully for table: {}", tableId);
        return mapToDTO(qrCode);
    }
    
    /**
     * Get all QR codes for current business
     */
    public List<QRCodeDTO> getAllQRCodes() {
        Long businessId = businessContext.getCurrentBusinessId();
        
        if (businessId == null) {
            // Admin sees all
            return qrCodeRepository.findAll().stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
        }
        
        return qrCodeRepository.findByBusinessId(businessId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get active QR codes for current business
     */
    public List<QRCodeDTO> getActiveQRCodes() {
        Long businessId = businessContext.getCurrentBusinessId();
        
        if (businessId == null) {
            throw new RuntimeException("Business context required");
        }
        
        return qrCodeRepository.findByBusinessIdAndIsActiveTrue(businessId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get QR code by ID
     */
    public QRCodeDTO getQRCodeById(Long id) {
        QRCode qrCode = qrCodeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("QR code not found"));
        
        // Validate business access
        businessContext.validateBusinessAccess(qrCode.getBusinessId());
        
        return mapToDTO(qrCode);
    }
    
    /**
     * Get QR code image as byte array for download
     */
    public byte[] getQRCodeImage(Long id) {
        QRCode qrCode = qrCodeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("QR code not found"));
        
        // Validate business access
        businessContext.validateBusinessAccess(qrCode.getBusinessId());
        
        // Decode Base64 image
        return Base64.getDecoder().decode(qrCode.getQrCodeImage().split(",")[1]);
    }
    
    /**
     * Deactivate QR code
     */
    @Transactional
    public void deactivateQRCode(Long id) {
        log.info("Deactivating QR code: {}", id);
        
        QRCode qrCode = qrCodeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("QR code not found"));
        
        // Validate business access
        businessContext.validateBusinessAccess(qrCode.getBusinessId());
        
        qrCode.setIsActive(false);
        qrCodeRepository.save(qrCode);
        
        log.info("QR code deactivated: {}", id);
    }
    
    /**
     * Reactivate QR code
     */
    @Transactional
    public void reactivateQRCode(Long id) {
        log.info("Reactivating QR code: {}", id);
        
        QRCode qrCode = qrCodeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("QR code not found"));
        
        // Validate business access
        businessContext.validateBusinessAccess(qrCode.getBusinessId());
        
        qrCode.setIsActive(true);
        qrCodeRepository.save(qrCode);
        
        log.info("QR code reactivated: {}", id);
    }
    
    /**
     * Delete QR code
     */
    @Transactional
    public void deleteQRCode(Long id) {
        log.info("Deleting QR code: {}", id);
        
        QRCode qrCode = qrCodeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("QR code not found"));
        
        // Validate business access
        businessContext.validateBusinessAccess(qrCode.getBusinessId());
        
        qrCodeRepository.delete(qrCode);
        
        log.info("QR code deleted: {}", id);
    }
    
    /**
     * Generate QR codes for all tables in business
     */
    @Transactional
    public List<QRCodeDTO> generateQRCodesForAllTables() {
        log.info("Generating QR codes for all tables");
        
        Long businessId = businessContext.getCurrentBusinessId();
        if (businessId == null) {
            throw new RuntimeException("Business context required");
        }
        
        List<RestaurantTable> tables = tableRepository.findByBusinessId(businessId);
        List<QRCodeDTO> generatedQRCodes = new ArrayList<>();
        
        for (RestaurantTable table : tables) {
            try {
                QRCodeDTO qrCode = generateQRCodeForTable(table.getId());
                generatedQRCodes.add(qrCode);
            } catch (Exception e) {
                log.error("Failed to generate QR code for table: {}", table.getId(), e);
            }
        }
        
        log.info("Generated {} QR codes", generatedQRCodes.size());
        return generatedQRCodes;
    }
    
    // ==================== HELPER METHODS ====================
    
    /**
     * Generate unique token for QR code
     */
    private String generateUniqueToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }
    
    /**
     * Generate QR code image as Base64 string using ZXing library
     */
    private String generateQRCodeImageBase64(String url, int width, int height) 
            throws WriterException, IOException {
        
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 1);
        
        BitMatrix bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, width, height, hints);
        BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
        
        // Convert to Base64
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", baos);
        byte[] imageBytes = baos.toByteArray();
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        
        return "data:image/png;base64," + base64Image;
    }
    
    /**
     * Map QRCode entity to DTO
     */
    private QRCodeDTO mapToDTO(QRCode qrCode) {
        // Fetch table details separately if needed
        RestaurantTable table = tableRepository.findById(qrCode.getTableId()).orElse(null);
        
        return QRCodeDTO.builder()
                .id(qrCode.getId())
                .businessId(qrCode.getBusinessId())
                .tableId(qrCode.getTableId())
                .tableNumber(table != null ? table.getTableNumber() : null)
                .tableName(table != null ? table.getTableName() : null)
                .qrToken(qrCode.getQrToken())
                .qrCodeUrl(qrCode.getQrCodeUrl())
                .qrCodeImage(qrCode.getQrCodeImage())
                .isActive(qrCode.getIsActive())
                .scanCount(qrCode.getScanCount())
                .lastScannedAt(qrCode.getLastScannedAt())
                .createdAt(qrCode.getCreatedAt())
                .build();
    }
}
