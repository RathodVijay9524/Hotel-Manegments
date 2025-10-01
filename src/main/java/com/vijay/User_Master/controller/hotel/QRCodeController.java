package com.vijay.User_Master.controller.hotel;

import com.vijay.User_Master.dto.hotel.QRCodeDTO;
import com.vijay.User_Master.service.hotel.QRCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * QRCodeController - QR Code Management for Business Owners
 * Allows owners to generate, view, download and manage QR codes for their tables
 */
@RestController
@RequestMapping("/api/hotel/qr-codes")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'MANAGER')")
public class QRCodeController {
    
    private final QRCodeService qrCodeService;
    
    /**
     * Generate QR code for a specific table
     * If QR already exists, returns existing one
     * 
     * POST /api/hotel/qr-codes/generate/5
     */
    @PostMapping("/generate/{tableId}")
    public ResponseEntity<QRCodeDTO> generateQRCode(@PathVariable Long tableId) {
        log.info("Generating QR code for table: {}", tableId);
        QRCodeDTO qrCode = qrCodeService.generateQRCodeForTable(tableId);
        return ResponseEntity.ok(qrCode);
    }
    
    /**
     * Generate QR codes for ALL tables in business
     * Convenient for initial setup
     * 
     * POST /api/hotel/qr-codes/generate-all
     */
    @PostMapping("/generate-all")
    public ResponseEntity<QRCodesResponse> generateAllQRCodes() {
        log.info("Generating QR codes for all tables");
        List<QRCodeDTO> qrCodes = qrCodeService.generateQRCodesForAllTables();
        return ResponseEntity.ok(new QRCodesResponse(qrCodes, qrCodes.size()));
    }
    
    /**
     * Get all QR codes for business
     * 
     * GET /api/hotel/qr-codes
     */
    @GetMapping
    public ResponseEntity<QRCodesResponse> getAllQRCodes() {
        log.info("Getting all QR codes");
        List<QRCodeDTO> qrCodes = qrCodeService.getAllQRCodes();
        return ResponseEntity.ok(new QRCodesResponse(qrCodes, qrCodes.size()));
    }
    
    /**
     * Get active QR codes only
     * 
     * GET /api/hotel/qr-codes/active
     */
    @GetMapping("/active")
    public ResponseEntity<QRCodesResponse> getActiveQRCodes() {
        log.info("Getting active QR codes");
        List<QRCodeDTO> qrCodes = qrCodeService.getActiveQRCodes();
        return ResponseEntity.ok(new QRCodesResponse(qrCodes, qrCodes.size()));
    }
    
    /**
     * Get specific QR code by ID
     * 
     * GET /api/hotel/qr-codes/1
     */
    @GetMapping("/{id}")
    public ResponseEntity<QRCodeDTO> getQRCode(@PathVariable Long id) {
        log.info("Getting QR code: {}", id);
        QRCodeDTO qrCode = qrCodeService.getQRCodeById(id);
        return ResponseEntity.ok(qrCode);
    }
    
    /**
     * Download QR code image as PNG
     * Can be printed and placed on tables
     * 
     * GET /api/hotel/qr-codes/1/download
     * Returns: PNG image file
     */
    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadQRCode(@PathVariable Long id) {
        log.info("Downloading QR code image: {}", id);
        
        byte[] qrImage = qrCodeService.getQRCodeImage(id);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setContentDispositionFormData("attachment", "qr-code-" + id + ".png");
        headers.setContentLength(qrImage.length);
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(qrImage);
    }
    
    /**
     * Deactivate QR code
     * Prevents it from being scanned (for security or maintenance)
     * 
     * PUT /api/hotel/qr-codes/1/deactivate
     */
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<MessageResponse> deactivateQRCode(@PathVariable Long id) {
        log.info("Deactivating QR code: {}", id);
        qrCodeService.deactivateQRCode(id);
        return ResponseEntity.ok(new MessageResponse("QR code deactivated successfully"));
    }
    
    /**
     * Reactivate QR code
     * Makes it scannable again
     * 
     * PUT /api/hotel/qr-codes/1/reactivate
     */
    @PutMapping("/{id}/reactivate")
    public ResponseEntity<MessageResponse> reactivateQRCode(@PathVariable Long id) {
        log.info("Reactivating QR code: {}", id);
        qrCodeService.reactivateQRCode(id);
        return ResponseEntity.ok(new MessageResponse("QR code reactivated successfully"));
    }
    
    /**
     * Delete QR code permanently
     * 
     * DELETE /api/hotel/qr-codes/1
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')") // Only owners and admins can delete
    public ResponseEntity<MessageResponse> deleteQRCode(@PathVariable Long id) {
        log.info("Deleting QR code: {}", id);
        qrCodeService.deleteQRCode(id);
        return ResponseEntity.ok(new MessageResponse("QR code deleted successfully"));
    }
    
    // ==================== RESPONSE WRAPPERS ====================
    
    /**
     * QR codes list response
     */
    public record QRCodesResponse(List<QRCodeDTO> qrCodes, int count) {}
    
    /**
     * Simple message response
     */
    public record MessageResponse(String message) {}
}
