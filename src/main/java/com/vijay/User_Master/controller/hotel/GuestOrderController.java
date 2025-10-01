package com.vijay.User_Master.controller.hotel;

import com.vijay.User_Master.dto.hotel.GuestOrderRequest;
import com.vijay.User_Master.dto.hotel.GuestSessionDTO;
import com.vijay.User_Master.dto.hotel.MenuItemDTO;
import com.vijay.User_Master.dto.hotel.OrderDTO;
import com.vijay.User_Master.service.hotel.GuestOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * GuestOrderController - PUBLIC API for contactless ordering
 * No authentication required - uses session tokens
 * Allows guests to order by scanning QR codes
 */
@RestController
@RequestMapping("/api/public/guest")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*") // Allow all origins for public API
public class GuestOrderController {
    
    private final GuestOrderService guestOrderService;
    
    /**
     * Scan QR code - Entry point for contactless ordering
     * Returns session token for subsequent requests
     * 
     * Example: GET /api/public/guest/scan?token=abc123xyz
     */
    @GetMapping("/scan")
    public ResponseEntity<GuestSessionDTO> scanQRCode(@RequestParam String token) {
        log.info("QR code scanned with token: {}", token);
        GuestSessionDTO session = guestOrderService.scanQRCode(token);
        return ResponseEntity.ok(session);
    }
    
    /**
     * Get menu for guest
     * Automatically filtered by business from session
     * 
     * Header: X-Guest-Session: guest-session-token
     */
    @GetMapping("/menu")
    public ResponseEntity<MenuResponse> getMenu(
            @RequestHeader("X-Guest-Session") String sessionToken
    ) {
        log.info("Getting menu for guest session: {}", sessionToken);
        List<MenuItemDTO> menu = guestOrderService.getMenuForGuest(sessionToken);
        return ResponseEntity.ok(new MenuResponse(menu, menu.size()));
    }
    
    /**
     * Get menu by category for guest
     * 
     * Header: X-Guest-Session: guest-session-token
     */
    @GetMapping("/menu/category/{categoryId}")
    public ResponseEntity<MenuResponse> getMenuByCategory(
            @PathVariable Long categoryId,
            @RequestHeader("X-Guest-Session") String sessionToken
    ) {
        log.info("Getting menu category {} for guest session: {}", categoryId, sessionToken);
        List<MenuItemDTO> menu = guestOrderService.getMenuByCategoryForGuest(sessionToken, categoryId);
        return ResponseEntity.ok(new MenuResponse(menu, menu.size()));
    }
    
    /**
     * Place order as guest
     * Order automatically assigned to correct business
     * 
     * Header: X-Guest-Session: guest-session-token
     */
    @PostMapping("/orders")
    public ResponseEntity<OrderDTO> placeOrder(
            @RequestHeader("X-Guest-Session") String sessionToken,
            @RequestBody GuestOrderRequest request
    ) {
        log.info("Guest placing order with session: {}", sessionToken);
        OrderDTO order = guestOrderService.placeGuestOrder(sessionToken, request);
        return ResponseEntity.ok(order);
    }
    
    /**
     * Get order status
     * Guest can track their order in real-time
     * 
     * Header: X-Guest-Session: guest-session-token
     */
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderDTO> getOrderStatus(
            @PathVariable Long orderId,
            @RequestHeader("X-Guest-Session") String sessionToken
    ) {
        log.info("Getting order status: {} for session: {}", orderId, sessionToken);
        OrderDTO order = guestOrderService.getOrderStatus(orderId, sessionToken);
        return ResponseEntity.ok(order);
    }
    
    /**
     * Get all orders for current session
     * 
     * Header: X-Guest-Session: guest-session-token
     */
    @GetMapping("/orders")
    public ResponseEntity<List<OrderDTO>> getGuestOrders(
            @RequestHeader("X-Guest-Session") String sessionToken
    ) {
        log.info("Getting all orders for session: {}", sessionToken);
        List<OrderDTO> orders = guestOrderService.getGuestOrders(sessionToken);
        return ResponseEntity.ok(orders);
    }
    
    /**
     * Complete session (when guest pays and leaves)
     * 
     * Header: X-Guest-Session: guest-session-token
     */
    @PostMapping("/complete")
    public ResponseEntity<Void> completeSession(
            @RequestHeader("X-Guest-Session") String sessionToken
    ) {
        log.info("Completing guest session: {}", sessionToken);
        guestOrderService.completeGuestSession(sessionToken);
        return ResponseEntity.ok().build();
    }
    
    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<HealthResponse> health() {
        return ResponseEntity.ok(new HealthResponse("Guest ordering API is running", "OK"));
    }
    
    // ==================== RESPONSE WRAPPERS ====================
    
    /**
     * Menu response wrapper
     */
    public record MenuResponse(List<MenuItemDTO> items, int count) {}
    
    /**
     * Health check response
     */
    public record HealthResponse(String message, String status) {}
}
