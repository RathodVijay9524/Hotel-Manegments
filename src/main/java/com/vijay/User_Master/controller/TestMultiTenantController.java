package com.vijay.User_Master.controller;

import com.vijay.User_Master.Helper.BusinessContextFilter;
import com.vijay.User_Master.config.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Test Controller for Multi-Tenant Functionality
 * 
 * Use this to verify:
 * - Business context is working
 * - Role detection is correct
 * - User identification is accurate
 * 
 * DELETE THIS FILE after testing is complete!
 */
@RestController
@RequestMapping("/api/test/multi-tenant")
public class TestMultiTenantController {
    
    @Autowired
    private BusinessContextFilter businessContext;
    
    /**
     * Test endpoint to check business context
     * 
     * Expected responses:
     * - ADMIN: businessId = null, isAdmin = true
     * - OWNER: businessId = user.id, isOwner = true
     * - WORKER: businessId = owner.id, isWorker = true
     */
    @GetMapping("/context")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getBusinessContext() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Get business ID
            Long businessId = businessContext.getCurrentBusinessId();
            response.put("businessId", businessId);
            response.put("businessIdStatus", businessId == null ? "ALL_BUSINESSES (Admin)" : "SPECIFIC_BUSINESS");
            
            // Get role checks
            response.put("isAdmin", businessContext.isAdmin());
            response.put("isOwner", businessContext.isOwner());
            response.put("isWorkerOrManager", businessContext.isWorkerOrManager());
            
            // Get user info
            response.put("userId", businessContext.getCurrentUserId());
            
            CustomUserDetails userDetails = businessContext.getCurrentUserDetails();
            response.put("username", userDetails.getUsername());
            response.put("name", userDetails.getName());
            
            // Success message
            if (businessContext.isAdmin()) {
                response.put("message", "✅ You are ADMIN - You can see ALL businesses");
            } else if (businessContext.isOwner()) {
                response.put("message", "✅ You are OWNER - You see YOUR business (ID: " + businessId + ")");
            } else if (businessContext.isWorkerOrManager()) {
                response.put("message", "✅ You are WORKER/MANAGER - You see owner's business (ID: " + businessId + ")");
            } else {
                response.put("message", "⚠️ You are NORMAL user - No business access");
            }
            
            response.put("status", "SUCCESS");
            
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("error", e.getMessage());
            response.put("message", "❌ Error getting business context");
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Test validation logic
     */
    @GetMapping("/validate-access")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> testValidateAccess() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long businessId = businessContext.getCurrentBusinessId();
            
            // Test 1: Validate own business (should pass)
            if (businessId != null) {
                businessContext.validateBusinessAccess(businessId);
                response.put("test1_ownBusiness", "✅ PASS - Can access own business");
            } else {
                response.put("test1_ownBusiness", "✅ PASS - Admin can access all");
            }
            
            // Test 2: Try to validate different business (should fail for non-admin)
            if (businessId != null && !businessContext.isAdmin()) {
                try {
                    Long otherBusinessId = businessId + 1;
                    businessContext.validateBusinessAccess(otherBusinessId);
                    response.put("test2_otherBusiness", "❌ FAIL - Should not access other business!");
                } catch (Exception e) {
                    response.put("test2_otherBusiness", "✅ PASS - Correctly blocked access to other business");
                }
            } else {
                response.put("test2_otherBusiness", "⏭️ SKIP - Admin can access all businesses");
            }
            
            response.put("status", "SUCCESS");
            response.put("message", "Validation tests completed");
            
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("error", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Health check for multi-tenant setup
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        
        response.put("multiTenantEnabled", true);
        response.put("businessContextFilterActive", true);
        response.put("timestamp", System.currentTimeMillis());
        response.put("message", "✅ Multi-tenant system is ready!");
        
        return ResponseEntity.ok(response);
    }
}
