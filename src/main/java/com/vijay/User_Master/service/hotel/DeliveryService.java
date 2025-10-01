package com.vijay.User_Master.service.hotel;

import com.vijay.User_Master.dto.hotel.DeliveryAgentDTO;
import com.vijay.User_Master.dto.hotel.DeliveryTrackingDTO;
import com.vijay.User_Master.dto.hotel.UpdateLocationRequest;
import com.vijay.User_Master.entity.hotel.DeliveryAgent;
import com.vijay.User_Master.entity.hotel.DeliveryTracking;
import com.vijay.User_Master.entity.hotel.Order;
import com.vijay.User_Master.repository.hotel.DeliveryAgentRepository;
import com.vijay.User_Master.repository.hotel.DeliveryTrackingRepository;
import com.vijay.User_Master.repository.hotel.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryService {
    
    private final DeliveryTrackingRepository deliveryTrackingRepository;
    private final DeliveryAgentRepository deliveryAgentRepository;
    private final OrderRepository orderRepository;
    
    // ==================== DELIVERY AGENT OPERATIONS ====================
    
    @Transactional
    public DeliveryAgentDTO createAgent(DeliveryAgentDTO dto) {
        log.info("Creating delivery agent: {}", dto.getName());
        
        DeliveryAgent agent = DeliveryAgent.builder()
                .name(dto.getName())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .vehicleType(dto.getVehicleType())
                .vehicleNumber(dto.getVehicleNumber())
                .isAvailable(true)
                .isOnline(false)
                .rating(0.0)
                .totalDeliveries(0)
                .build();
        
        agent = deliveryAgentRepository.save(agent);
        return mapToAgentDTO(agent);
    }
    
    @Transactional
    public DeliveryAgentDTO updateAgentStatus(Long agentId, Boolean isAvailable, Boolean isOnline) {
        log.info("Updating agent {} status: available={}, online={}", agentId, isAvailable, isOnline);
        
        DeliveryAgent agent = deliveryAgentRepository.findById(agentId)
                .orElseThrow(() -> new RuntimeException("Agent not found"));
        
        if (isAvailable != null) agent.setIsAvailable(isAvailable);
        if (isOnline != null) agent.setIsOnline(isOnline);
        
        agent = deliveryAgentRepository.save(agent);
        return mapToAgentDTO(agent);
    }
    
    @Transactional
    public DeliveryAgentDTO updateAgentLocation(Long agentId, UpdateLocationRequest request) {
        DeliveryAgent agent = deliveryAgentRepository.findById(agentId)
                .orElseThrow(() -> new RuntimeException("Agent not found"));
        
        agent.setCurrentLatitude(request.getLatitude());
        agent.setCurrentLongitude(request.getLongitude());
        
        agent = deliveryAgentRepository.save(agent);
        return mapToAgentDTO(agent);
    }
    
    public List<DeliveryAgentDTO> getAllAgents() {
        return deliveryAgentRepository.findAll().stream()
                .map(this::mapToAgentDTO)
                .collect(Collectors.toList());
    }
    
    public List<DeliveryAgentDTO> getAvailableAgents() {
        return deliveryAgentRepository.findByIsAvailableTrueAndIsOnlineTrue().stream()
                .map(this::mapToAgentDTO)
                .collect(Collectors.toList());
    }
    
    // ==================== DELIVERY TRACKING OPERATIONS ====================
    
    @Transactional
    public DeliveryTrackingDTO createDeliveryTracking(Long orderId, Double pickupLat, Double pickupLng, 
                                                       Double deliveryLat, Double deliveryLng) {
        log.info("Creating delivery tracking for order: {}", orderId);
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        // Check if tracking already exists
        deliveryTrackingRepository.findByOrderId(orderId).ifPresent(dt -> {
            throw new RuntimeException("Delivery tracking already exists for this order");
        });
        
        DeliveryTracking tracking = DeliveryTracking.builder()
                .order(order)
                .status(DeliveryTracking.DeliveryStatus.PENDING)
                .pickupLatitude(pickupLat)
                .pickupLongitude(pickupLng)
                .deliveryLatitude(deliveryLat)
                .deliveryLongitude(deliveryLng)
                .estimatedTimeMinutes(30)
                .build();
        
        tracking = deliveryTrackingRepository.save(tracking);
        return mapToTrackingDTO(tracking);
    }
    
    @Transactional
    public DeliveryTrackingDTO assignAgent(Long trackingId, Long agentId) {
        log.info("Assigning agent {} to delivery {}", agentId, trackingId);
        
        DeliveryTracking tracking = deliveryTrackingRepository.findById(trackingId)
                .orElseThrow(() -> new RuntimeException("Delivery tracking not found"));
        
        DeliveryAgent agent = deliveryAgentRepository.findById(agentId)
                .orElseThrow(() -> new RuntimeException("Agent not found"));
        
        if (!agent.getIsAvailable() || !agent.getIsOnline()) {
            throw new RuntimeException("Agent is not available for delivery");
        }
        
        tracking.setAgent(agent);
        tracking.setStatus(DeliveryTracking.DeliveryStatus.ASSIGNED);
        tracking.setAssignedAt(LocalDateTime.now());
        
        agent.setIsAvailable(false); // Mark agent as busy
        deliveryAgentRepository.save(agent);
        
        tracking = deliveryTrackingRepository.save(tracking);
        return mapToTrackingDTO(tracking);
    }
    
    @Transactional
    public DeliveryTrackingDTO autoAssignAgent(Long trackingId) {
        log.info("Auto-assigning agent to delivery {}", trackingId);
        
        List<DeliveryAgent> availableAgents = deliveryAgentRepository.findAvailableAgentsForAssignment();
        
        if (availableAgents.isEmpty()) {
            throw new RuntimeException("No available agents for delivery");
        }
        
        // Assign to agent with least deliveries
        DeliveryAgent agent = availableAgents.get(0);
        return assignAgent(trackingId, agent.getId());
    }
    
    @Transactional
    public DeliveryTrackingDTO updateDeliveryStatus(Long trackingId, String status) {
        log.info("Updating delivery {} status to {}", trackingId, status);
        
        DeliveryTracking tracking = deliveryTrackingRepository.findById(trackingId)
                .orElseThrow(() -> new RuntimeException("Delivery tracking not found"));
        
        DeliveryTracking.DeliveryStatus newStatus = DeliveryTracking.DeliveryStatus.valueOf(status);
        tracking.setStatus(newStatus);
        
        switch (newStatus) {
            case PICKED_UP:
                tracking.setPickedUpAt(LocalDateTime.now());
                break;
            case DELIVERED:
                tracking.setDeliveredAt(LocalDateTime.now());
                // Mark agent as available again
                if (tracking.getAgent() != null) {
                    DeliveryAgent agent = tracking.getAgent();
                    agent.setIsAvailable(true);
                    agent.setTotalDeliveries(agent.getTotalDeliveries() + 1);
                    deliveryAgentRepository.save(agent);
                }
                // Update order status
                Order order = tracking.getOrder();
                order.setStatus(Order.OrderStatus.DELIVERED);
                orderRepository.save(order);
                break;
        }
        
        tracking = deliveryTrackingRepository.save(tracking);
        return mapToTrackingDTO(tracking);
    }
    
    @Transactional
    public DeliveryTrackingDTO updateCurrentLocation(Long trackingId, UpdateLocationRequest request) {
        DeliveryTracking tracking = deliveryTrackingRepository.findById(trackingId)
                .orElseThrow(() -> new RuntimeException("Delivery tracking not found"));
        
        tracking.setCurrentLatitude(request.getLatitude());
        tracking.setCurrentLongitude(request.getLongitude());
        
        // Also update agent location
        if (tracking.getAgent() != null) {
            DeliveryAgent agent = tracking.getAgent();
            agent.setCurrentLatitude(request.getLatitude());
            agent.setCurrentLongitude(request.getLongitude());
            deliveryAgentRepository.save(agent);
        }
        
        tracking = deliveryTrackingRepository.save(tracking);
        return mapToTrackingDTO(tracking);
    }
    
    public DeliveryTrackingDTO getTrackingByOrderId(Long orderId) {
        DeliveryTracking tracking = deliveryTrackingRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Delivery tracking not found for order"));
        return mapToTrackingDTO(tracking);
    }
    
    public List<DeliveryTrackingDTO> getActiveDeliveries() {
        return deliveryTrackingRepository.findAllActiveDeliveries().stream()
                .map(this::mapToTrackingDTO)
                .collect(Collectors.toList());
    }
    
    public List<DeliveryTrackingDTO> getAgentDeliveries(Long agentId) {
        return deliveryTrackingRepository.findByAgentId(agentId).stream()
                .map(this::mapToTrackingDTO)
                .collect(Collectors.toList());
    }
    
    // ==================== MAPPERS ====================
    
    private DeliveryAgentDTO mapToAgentDTO(DeliveryAgent agent) {
        return DeliveryAgentDTO.builder()
                .id(agent.getId())
                .name(agent.getName())
                .phone(agent.getPhone())
                .email(agent.getEmail())
                .vehicleType(agent.getVehicleType())
                .vehicleNumber(agent.getVehicleNumber())
                .isAvailable(agent.getIsAvailable())
                .isOnline(agent.getIsOnline())
                .currentLatitude(agent.getCurrentLatitude())
                .currentLongitude(agent.getCurrentLongitude())
                .rating(agent.getRating())
                .totalDeliveries(agent.getTotalDeliveries())
                .build();
    }
    
    private DeliveryTrackingDTO mapToTrackingDTO(DeliveryTracking tracking) {
        return DeliveryTrackingDTO.builder()
                .id(tracking.getId())
                .orderId(tracking.getOrder().getId())
                .orderNumber(tracking.getOrder().getOrderNumber())
                .agentId(tracking.getAgent() != null ? tracking.getAgent().getId() : null)
                .agentName(tracking.getAgent() != null ? tracking.getAgent().getName() : null)
                .agentPhone(tracking.getAgent() != null ? tracking.getAgent().getPhone() : null)
                .status(tracking.getStatus().name())
                .pickupLatitude(tracking.getPickupLatitude())
                .pickupLongitude(tracking.getPickupLongitude())
                .deliveryLatitude(tracking.getDeliveryLatitude())
                .deliveryLongitude(tracking.getDeliveryLongitude())
                .currentLatitude(tracking.getCurrentLatitude())
                .currentLongitude(tracking.getCurrentLongitude())
                .assignedAt(tracking.getAssignedAt())
                .pickedUpAt(tracking.getPickedUpAt())
                .deliveredAt(tracking.getDeliveredAt())
                .estimatedTimeMinutes(tracking.getEstimatedTimeMinutes())
                .distanceKm(tracking.getDistanceKm())
                .deliveryNotes(tracking.getDeliveryNotes())
                .deliveryRating(tracking.getDeliveryRating())
                .createdAt(tracking.getCreatedAt())
                .build();
    }
}
