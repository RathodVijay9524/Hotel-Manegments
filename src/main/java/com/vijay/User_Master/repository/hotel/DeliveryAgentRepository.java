package com.vijay.User_Master.repository.hotel;

import com.vijay.User_Master.entity.hotel.DeliveryAgent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryAgentRepository extends JpaRepository<DeliveryAgent, Long> {
    
    Optional<DeliveryAgent> findByPhone(String phone);
    
    Optional<DeliveryAgent> findByEmail(String email);
    
    List<DeliveryAgent> findByIsAvailableTrueAndIsOnlineTrue();
    
    List<DeliveryAgent> findByIsOnlineTrue();
    
    @Query("SELECT da FROM DeliveryAgent da WHERE da.isAvailable = true AND da.isOnline = true ORDER BY da.totalDeliveries ASC")
    List<DeliveryAgent> findAvailableAgentsForAssignment();
    
    @Query("SELECT da FROM DeliveryAgent da ORDER BY da.rating DESC")
    List<DeliveryAgent> findTopRatedAgents();
}
