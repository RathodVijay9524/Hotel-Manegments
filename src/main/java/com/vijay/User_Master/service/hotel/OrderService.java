package com.vijay.User_Master.service.hotel;

import com.vijay.User_Master.dto.hotel.CreateOrderRequest;
import com.vijay.User_Master.dto.hotel.OrderDTO;
import com.vijay.User_Master.dto.hotel.OrderItemDTO;
import com.vijay.User_Master.entity.hotel.*;
import com.vijay.User_Master.repository.hotel.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final MenuItemRepository menuItemRepository;
    private final RestaurantTableRepository tableRepository;
    
    @Transactional
    public OrderDTO createOrder(CreateOrderRequest request) {
        log.info("Creating new order for user: {}", request.getUserId());
        
        // Generate unique order number
        String orderNumber = generateOrderNumber();
        
        // Get table if dine-in
        RestaurantTable table = null;
        if (request.getTableId() != null) {
            table = tableRepository.findById(request.getTableId())
                    .orElseThrow(() -> new RuntimeException("Table not found"));
        }
        
        // Calculate totals
        BigDecimal subtotal = BigDecimal.ZERO;
        
        // Create order
        Order order = Order.builder()
                .orderNumber(orderNumber)
                .userId(request.getUserId())
                .customerName(request.getCustomerName())
                .customerPhone(request.getCustomerPhone())
                .table(table)
                .orderType(Order.OrderType.valueOf(request.getOrderType()))
                .status(Order.OrderStatus.PENDING)
                .specialInstructions(request.getSpecialInstructions())
                .deliveryAddress(request.getDeliveryAddress())
                .build();
        
        order = orderRepository.save(order);
        
        // Create order items
        for (CreateOrderRequest.OrderItemRequest itemRequest : request.getItems()) {
            MenuItem menuItem = menuItemRepository.findById(itemRequest.getMenuItemId())
                    .orElseThrow(() -> new RuntimeException("Menu item not found"));
            
            BigDecimal itemTotal = menuItem.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
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
            
            // Update menu item total orders
            menuItem.setTotalOrders(menuItem.getTotalOrders() + itemRequest.getQuantity());
            menuItemRepository.save(menuItem);
        }
        
        // Calculate tax and total
        BigDecimal tax = subtotal.multiply(BigDecimal.valueOf(0.05)); // 5% tax
        BigDecimal totalAmount = subtotal.add(tax);
        
        order.setSubtotal(subtotal);
        order.setTax(tax);
        order.setTotalAmount(totalAmount);
        
        // Set estimated delivery time (30 minutes from now)
        order.setEstimatedDeliveryTime(LocalDateTime.now().plusMinutes(30));
        
        order = orderRepository.save(order);
        
        log.info("Order created successfully: {}", orderNumber);
        return mapToOrderDTO(order);
    }
    
    @Transactional
    public OrderDTO updateOrderStatus(Long orderId, String status) {
        log.info("Updating order {} status to {}", orderId, status);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        order.setStatus(Order.OrderStatus.valueOf(status));
        
        if (status.equals("COMPLETED") || status.equals("DELIVERED")) {
            order.setCompletedAt(LocalDateTime.now());
            
            // Mark table as available if dine-in
            if (order.getTable() != null) {
                order.getTable().setIsAvailable(true);
                tableRepository.save(order.getTable());
            }
        }
        
        order = orderRepository.save(order);
        return mapToOrderDTO(order);
    }
    
    public OrderDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return mapToOrderDTO(order);
    }
    
    public OrderDTO getOrderByNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return mapToOrderDTO(order);
    }
    
    public List<OrderDTO> getOrdersByUser(Long userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(this::mapToOrderDTO)
                .collect(Collectors.toList());
    }
    
    public Page<OrderDTO> getOrdersByUser(Long userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable)
                .map(this::mapToOrderDTO);
    }
    
    public List<OrderDTO> getOrdersByStatus(String status) {
        return orderRepository.findByStatus(Order.OrderStatus.valueOf(status)).stream()
                .map(this::mapToOrderDTO)
                .collect(Collectors.toList());
    }
    
    public Page<OrderDTO> getOrdersByStatus(String status, Pageable pageable) {
        return orderRepository.findByStatus(Order.OrderStatus.valueOf(status), pageable)
                .map(this::mapToOrderDTO);
    }
    
    public List<OrderDTO> getActiveOrders() {
        List<Order> orders = orderRepository.findAll().stream()
                .filter(o -> !o.getStatus().equals(Order.OrderStatus.COMPLETED) 
                          && !o.getStatus().equals(Order.OrderStatus.CANCELLED))
                .collect(Collectors.toList());
        return orders.stream().map(this::mapToOrderDTO).collect(Collectors.toList());
    }
    
    @Transactional
    public void cancelOrder(Long orderId) {
        log.info("Cancelling order: {}", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        order.setStatus(Order.OrderStatus.CANCELLED);
        
        // Mark table as available if dine-in
        if (order.getTable() != null) {
            order.getTable().setIsAvailable(true);
            tableRepository.save(order.getTable());
        }
        
        orderRepository.save(order);
    }
    
    // ==================== HELPER METHODS ====================
    
    private String generateOrderNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "ORD-" + timestamp;
    }
    
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
                .deliveryAddress(order.getDeliveryAddress())
                .estimatedDeliveryTime(order.getEstimatedDeliveryTime())
                .completedAt(order.getCompletedAt())
                .createdAt(order.getCreatedAt())
                .build();
    }
    
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
