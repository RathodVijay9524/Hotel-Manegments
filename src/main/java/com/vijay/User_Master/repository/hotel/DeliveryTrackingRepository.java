package com.vijay.User_Master.repository.hotel;

import com.vijay.User_Master.entity.hotel.DeliveryTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryTrackingRepository extends JpaRepository<DeliveryTracking, Long> {
    
    Optional<DeliveryTracking> findByOrderId(Long orderId);
    
    List<DeliveryTracking> findByAgentId(Long agentId);
    
    List<DeliveryTracking> findByStatus(DeliveryTracking.DeliveryStatus status);
    
    @Query("SELECT dt FROM DeliveryTracking dt WHERE dt.agent.id = :agentId AND dt.status IN ('ASSIGNED', 'PICKED_UP', 'IN_TRANSIT')")
    List<DeliveryTracking> findActiveDeliveriesByAgent(@Param("agentId") Long agentId);
    
    @Query("SELECT dt FROM DeliveryTracking dt WHERE dt.status IN ('PENDING', 'ASSIGNED', 'PICKED_UP', 'IN_TRANSIT')")
    List<DeliveryTracking> findAllActiveDeliveries();
    
    @Query("SELECT COUNT(dt) FROM DeliveryTracking dt WHERE dt.agent.id = :agentId AND dt.status = 'DELIVERED'")
    Long countCompletedDeliveriesByAgent(@Param("agentId") Long agentId);
}
