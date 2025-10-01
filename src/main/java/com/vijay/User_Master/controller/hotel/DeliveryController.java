package com.vijay.User_Master.controller.hotel;

import com.vijay.User_Master.dto.hotel.DeliveryAgentDTO;
import com.vijay.User_Master.dto.hotel.DeliveryTrackingDTO;
import com.vijay.User_Master.dto.hotel.UpdateLocationRequest;
import com.vijay.User_Master.service.hotel.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hotel/delivery")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DeliveryController {
    
    private final DeliveryService deliveryService;
    
    // ==================== DELIVERY AGENT ENDPOINTS ====================
    
    @PostMapping("/agents")
    public ResponseEntity<DeliveryAgentDTO> createAgent(@RequestBody DeliveryAgentDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(deliveryService.createAgent(dto));
    }
    
    @GetMapping("/agents")
    public ResponseEntity<List<DeliveryAgentDTO>> getAllAgents() {
        return ResponseEntity.ok(deliveryService.getAllAgents());
    }
    
    @GetMapping("/agents/available")
    public ResponseEntity<List<DeliveryAgentDTO>> getAvailableAgents() {
        return ResponseEntity.ok(deliveryService.getAvailableAgents());
    }
    
    @PatchMapping("/agents/{id}/status")
    public ResponseEntity<DeliveryAgentDTO> updateAgentStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> status) {
        return ResponseEntity.ok(deliveryService.updateAgentStatus(
                id, 
                status.get("isAvailable"), 
                status.get("isOnline")
        ));
    }
    
    @PatchMapping("/agents/{id}/location")
    public ResponseEntity<DeliveryAgentDTO> updateAgentLocation(
            @PathVariable Long id,
            @RequestBody UpdateLocationRequest request) {
        return ResponseEntity.ok(deliveryService.updateAgentLocation(id, request));
    }
    
    // ==================== DELIVERY TRACKING ENDPOINTS ====================
    
    @PostMapping("/tracking")
    public ResponseEntity<DeliveryTrackingDTO> createTracking(@RequestBody Map<String, Object> request) {
        Long orderId = Long.valueOf(request.get("orderId").toString());
        Double pickupLat = Double.valueOf(request.get("pickupLatitude").toString());
        Double pickupLng = Double.valueOf(request.get("pickupLongitude").toString());
        Double deliveryLat = Double.valueOf(request.get("deliveryLatitude").toString());
        Double deliveryLng = Double.valueOf(request.get("deliveryLongitude").toString());
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(deliveryService.createDeliveryTracking(orderId, pickupLat, pickupLng, deliveryLat, deliveryLng));
    }
    
    @PostMapping("/tracking/{id}/assign/{agentId}")
    public ResponseEntity<DeliveryTrackingDTO> assignAgent(
            @PathVariable Long id,
            @PathVariable Long agentId) {
        return ResponseEntity.ok(deliveryService.assignAgent(id, agentId));
    }
    
    @PostMapping("/tracking/{id}/auto-assign")
    public ResponseEntity<DeliveryTrackingDTO> autoAssignAgent(@PathVariable Long id) {
        return ResponseEntity.ok(deliveryService.autoAssignAgent(id));
    }
    
    @PatchMapping("/tracking/{id}/status")
    public ResponseEntity<DeliveryTrackingDTO> updateDeliveryStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        return ResponseEntity.ok(deliveryService.updateDeliveryStatus(id, request.get("status")));
    }
    
    @PatchMapping("/tracking/{id}/location")
    public ResponseEntity<DeliveryTrackingDTO> updateCurrentLocation(
            @PathVariable Long id,
            @RequestBody UpdateLocationRequest request) {
        return ResponseEntity.ok(deliveryService.updateCurrentLocation(id, request));
    }
    
    @GetMapping("/tracking/order/{orderId}")
    public ResponseEntity<DeliveryTrackingDTO> getTrackingByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(deliveryService.getTrackingByOrderId(orderId));
    }
    
    @GetMapping("/tracking/active")
    public ResponseEntity<List<DeliveryTrackingDTO>> getActiveDeliveries() {
        return ResponseEntity.ok(deliveryService.getActiveDeliveries());
    }
    
    @GetMapping("/tracking/agent/{agentId}")
    public ResponseEntity<List<DeliveryTrackingDTO>> getAgentDeliveries(@PathVariable Long agentId) {
        return ResponseEntity.ok(deliveryService.getAgentDeliveries(agentId));
    }
}
