package com.vijay.User_Master.service.hotel;

import com.vijay.User_Master.dto.hotel.*;
import com.vijay.User_Master.entity.hotel.*;
import com.vijay.User_Master.repository.hotel.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * GuestOrderService - Handles contactless ordering for guests via QR codes
 * No authentication required - uses session tokens instead
 * Automatically inherits business_id from QR code for perfect isolation
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GuestOrderService {
    
    private final QRCodeRepository qrCodeRepository;
    private final GuestSessionRepository sessionRepository;
    private final MenuItemRepository menuItemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final RestaurantTableRepository tableRepository;
    
    /**
     * Scan QR code and create guest session
     * This is the entry point for contactless ordering
     */
    @Transactional
    public GuestSessionDTO scanQRCode(String qrToken) {
        log.info("Scanning QR code with token: {}", qrToken);
        
        // Validate QR code
        QRCode qrCode = qrCodeRepository.findByQrToken(qrToken)
                .orElseThrow(() -> new RuntimeException("Invalid QR code"));
        
        if (!qrCode.getIsActive()) {
            throw new RuntimeException("QR code is inactive");
        }
        
        // Update scan count
        qrCode.incrementScanCount();
        qrCodeRepository.save(qrCode);
        
        // Check if there's already an active session for this table
        Optional<GuestSession> existingSession = sessionRepository
                .findActiveSessionForTable(qrCode.getTableId(), LocalDateTime.now());
        
        if (existingSession.isPresent()) {
            log.info("Returning existing session for table: {}", qrCode.getTableId());
            return mapToSessionDTO(existingSession.get());
        }
        
        // Create new guest session
        String sessionToken = generateSessionToken();
        
        GuestSession session = GuestSession.builder()
                .businessId(qrCode.getBusinessId())  // KEY: Inherited from QR code!
                .tableId(qrCode.getTableId())        // KEY: Inherited from QR code!
                .sessionToken(sessionToken)
                .status(GuestSession.SessionStatus.ACTIVE)
                .expiresAt(LocalDateTime.now().plusHours(3)) // 3-hour session
                .build();
        
        session = sessionRepository.save(session);
        
        log.info("Guest session created: {} for business: {} table: {}", 
                sessionToken, session.getBusinessId(), session.getTableId());
        
        return mapToSessionDTO(session);
    }
    
    /**
     * Get menu for guest - automatically filtered by business_id from session
     */
    public List<MenuItemDTO> getMenuForGuest(String sessionToken) {
        log.info("Getting menu for guest session: {}", sessionToken);
        
        GuestSession session = validateSession(sessionToken);
        
        // Get menu items for THIS business only
        // This is where business isolation happens!
        List<MenuItem> menuItems = menuItemRepository
                .findByBusinessIdAndIsAvailableTrue(session.getBusinessId());
        
        log.info("Found {} menu items for business: {}", menuItems.size(), session.getBusinessId());
        
        return menuItems.stream()
                .map(this::mapToMenuItemDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get menu items by category for guest
     */
    public List<MenuItemDTO> getMenuByCategoryForGuest(String sessionToken, Long categoryId) {
        log.info("Getting menu category {} for guest session: {}", categoryId, sessionToken);
        
        GuestSession session = validateSession(sessionToken);
        
        List<MenuItem> menuItems = menuItemRepository
                .findByBusinessIdAndCategoryIdAndIsAvailableTrue(session.getBusinessId(), categoryId);
        
        return menuItems.stream()
                .map(this::mapToMenuItemDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Place order as guest
     * Order automatically gets business_id from guest session
     */
    @Transactional
    public OrderDTO placeGuestOrder(String sessionToken, GuestOrderRequest request) {
        log.info("Placing guest order for session: {}", sessionToken);
        
        GuestSession session = validateSession(sessionToken);
        
        // Validate guest info
        if (request.getGuestName() == null || request.getGuestName().trim().isEmpty()) {
            throw new RuntimeException("Guest name is required");
        }
        
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new RuntimeException("Order must contain at least one item");
        }
        
        // Generate order number
        String orderNumber = generateOrderNumber();
        
        // Get table
        RestaurantTable table = tableRepository.findById(session.getTableId())
                .orElseThrow(() -> new RuntimeException("Table not found"));
        
        // Calculate totals
        BigDecimal subtotal = BigDecimal.ZERO;
        
        // Create order with business_id from session
        Order order = Order.builder()
                .businessId(session.getBusinessId())  // KEY: Auto-assigned from session!
                .orderNumber(orderNumber)
                .userId(null)  // Guest order - no user
                .customerName(request.getGuestName())
                .customerPhone(request.getGuestPhone())
                .table(table)
                .orderType(Order.OrderType.DINE_IN)
                .status(Order.OrderStatus.PENDING)
                .specialInstructions(request.getSpecialInstructions())
                .build();
        
        order = orderRepository.save(order);
        
        // Create order items
        for (GuestOrderRequest.OrderItemRequest itemRequest : request.getItems()) {
            MenuItem menuItem = menuItemRepository.findById(itemRequest.getMenuItemId())
                    .orElseThrow(() -> new RuntimeException("Menu item not found"));
            
            // Verify menu item belongs to same business (security check)
            if (!menuItem.getBusinessId().equals(session.getBusinessId())) {
                throw new RuntimeException("Menu item does not belong to this business");
            }
            
            BigDecimal itemTotal = menuItem.getPrice()
                    .multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            subtotal = subtotal.add(itemTotal);
            
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .menuItem(menuItem)
                    .quantity(itemRequest.getQuantity())
                    .unitPrice(menuItem.getPrice())
                    .totalPrice(itemTotal)
                    .specialInstructions(itemRequest.getSpecialInstructions())
                    .status(OrderItem.ItemStatus.PENDING)
                    .build();
            
            orderItemRepository.save(orderItem);
            
            // Update menu item stats
            menuItem.setTotalOrders(menuItem.getTotalOrders() + itemRequest.getQuantity());
            menuItemRepository.save(menuItem);
        }
        
        // Calculate tax and total
        BigDecimal tax = subtotal.multiply(BigDecimal.valueOf(0.05)); // 5% tax
        BigDecimal totalAmount = subtotal.add(tax);
        
        order.setSubtotal(subtotal);
        order.setTax(tax);
        order.setTotalAmount(totalAmount);
        order.setEstimatedDeliveryTime(LocalDateTime.now().plusMinutes(30));
        
        order = orderRepository.save(order);
        
        // Update session with guest info
        session.setGuestName(request.getGuestName());
        session.setGuestPhone(request.getGuestPhone());
        session.setGuestEmail(request.getGuestEmail());
        sessionRepository.save(session);
        
        // Mark table as occupied
        table.setIsAvailable(false);
        tableRepository.save(table);
        
        log.info("Guest order placed successfully: {} for business: {}", 
                orderNumber, session.getBusinessId());
        
        return mapToOrderDTO(order);
    }
    
    /**
     * Get order status for guest
     */
    public OrderDTO getOrderStatus(Long orderId, String sessionToken) {
        log.info("Getting order status: {} for session: {}", orderId, sessionToken);
        
        GuestSession session = validateSession(sessionToken);
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        // Verify order belongs to same business as session (security)
        if (!order.getBusinessId().equals(session.getBusinessId())) {
            throw new RuntimeException("Access denied - Order belongs to different business");
        }
        
        // Verify order is for same table (optional - extra security)
        if (order.getTable() != null && !order.getTable().getId().equals(session.getTableId())) {
            throw new RuntimeException("Access denied - Order belongs to different table");
        }
        
        return mapToOrderDTO(order);
    }
    
    /**
     * Get all orders for current guest session
     */
    public List<OrderDTO> getGuestOrders(String sessionToken) {
        log.info("Getting orders for guest session: {}", sessionToken);
        
        GuestSession session = validateSession(sessionToken);
        
        // Get orders for this table and business
        List<Order> orders = orderRepository.findByBusinessId(session.getBusinessId())
                .stream()
                .filter(o -> o.getTable() != null && o.getTable().getId().equals(session.getTableId()))
                .filter(o -> o.getCreatedAt().isAfter(session.getCreatedAt()))
                .collect(Collectors.toList());
        
        return orders.stream()
                .map(this::mapToOrderDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Complete guest session (when guest pays and leaves)
     */
    @Transactional
    public void completeGuestSession(String sessionToken) {
        log.info("Completing guest session: {}", sessionToken);
        
        GuestSession session = sessionRepository.findBySessionToken(sessionToken)
                .orElseThrow(() -> new RuntimeException("Invalid session"));
        
        session.complete();
        sessionRepository.save(session);
        
        log.info("Guest session completed: {}", sessionToken);
    }
    
    // ==================== HELPER METHODS ====================
    
    /**
     * Validate guest session
     * Throws exception if session is invalid or expired
     */
    private GuestSession validateSession(String sessionToken) {
        GuestSession session = sessionRepository.findBySessionToken(sessionToken)
                .orElseThrow(() -> new RuntimeException("Invalid session token"));
        
        if (session.hasExpired()) {
            session.setStatus(GuestSession.SessionStatus.EXPIRED);
            sessionRepository.save(session);
            throw new RuntimeException("Session has expired. Please scan QR code again.");
        }
        
        if (session.getStatus() != GuestSession.SessionStatus.ACTIVE) {
            throw new RuntimeException("Session is not active");
        }
        
        return session;
    }
    
    /**
     * Generate unique session token
     */
    private String generateSessionToken() {
        return "guest-" + UUID.randomUUID().toString().replace("-", "");
    }
    
    /**
     * Generate order number
     */
    private String generateOrderNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "ORD-" + timestamp;
    }
    
    /**
     * Map GuestSession to DTO
     */
    private GuestSessionDTO mapToSessionDTO(GuestSession session) {
        // Fetch table details separately if needed
        RestaurantTable table = tableRepository.findById(session.getTableId()).orElse(null);
        
        return GuestSessionDTO.builder()
                .id(session.getId())
                .sessionToken(session.getSessionToken())
                .businessId(session.getBusinessId())
                .tableId(session.getTableId())
                .tableNumber(table != null ? table.getTableNumber() : null)
                .tableName(table != null ? table.getTableName() : null)
                .guestName(session.getGuestName())
                .guestPhone(session.getGuestPhone())
                .status(session.getStatus().name())
                .createdAt(session.getCreatedAt())
                .expiresAt(session.getExpiresAt())
                .build();
    }
    
    /**
     * Map MenuItem to DTO
     */
    private MenuItemDTO mapToMenuItemDTO(MenuItem menuItem) {
        return MenuItemDTO.builder()
                .id(menuItem.getId())
                .categoryId(menuItem.getCategory().getId())
                .categoryName(menuItem.getCategory().getName())
                .name(menuItem.getName())
                .description(menuItem.getDescription())
                .price(menuItem.getPrice())
                .imageUrl(menuItem.getImageUrl())
                .isVegetarian(menuItem.getIsVegetarian())
                .isVegan(menuItem.getIsVegan())
                .isSpicy(menuItem.getIsSpicy())
                .preparationTime(menuItem.getPreparationTime())
                .calories(menuItem.getCalories())
                .isAvailable(menuItem.getIsAvailable())
                .isFeatured(menuItem.getIsFeatured())
                .rating(menuItem.getRating())
                .totalOrders(menuItem.getTotalOrders())
                .build();
    }
    
    /**
     * Map Order to DTO
     */
    private OrderDTO mapToOrderDTO(Order order) {
        List<OrderItemDTO> items = orderItemRepository.findByOrderId(order.getId()).stream()
                .map(this::mapToOrderItemDTO)
                .collect(Collectors.toList());
        
        return OrderDTO.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .userId(order.getUserId())
                .customerName(order.getCustomerName())
                .customerPhone(order.getCustomerPhone())
                .tableId(order.getTable() != null ? order.getTable().getId() : null)
                .tableNumber(order.getTable() != null ? order.getTable().getTableNumber() : null)
                .orderType(order.getOrderType().name())
                .status(order.getStatus().name())
                .items(items)
                .subtotal(order.getSubtotal())
                .tax(order.getTax())
                .discount(order.getDiscount())
                .totalAmount(order.getTotalAmount())
                .specialInstructions(order.getSpecialInstructions())
                .estimatedDeliveryTime(order.getEstimatedDeliveryTime())
                .completedAt(order.getCompletedAt())
                .createdAt(order.getCreatedAt())
                .build();
    }
    
    /**
     * Map OrderItem to DTO
     */
    private OrderItemDTO mapToOrderItemDTO(OrderItem item) {
        return OrderItemDTO.builder()
                .id(item.getId())
                .menuItemId(item.getMenuItem().getId())
                .menuItemName(item.getMenuItem().getName())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .totalPrice(item.getTotalPrice())
                .specialInstructions(item.getSpecialInstructions())
                .status(item.getStatus().name())
                .build();
    }
}
