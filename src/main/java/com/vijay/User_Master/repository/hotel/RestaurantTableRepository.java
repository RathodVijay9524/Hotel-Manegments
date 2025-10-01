package com.vijay.User_Master.repository.hotel;

import com.vijay.User_Master.entity.hotel.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Long> {
    
    // ========== MULTI-TENANT QUERIES ==========
    
    List<RestaurantTable> findByBusinessId(Long businessId);
    
    List<RestaurantTable> findByBusinessIdAndIsAvailableTrue(Long businessId);
    
    Optional<RestaurantTable> findByBusinessIdAndTableNumber(Long businessId, String tableNumber);
    
    List<RestaurantTable> findByBusinessIdAndLocation(Long businessId, String location);
    
    List<RestaurantTable> findByBusinessIdAndCapacityGreaterThanEqual(Long businessId, Integer capacity);
    
    Long countByBusinessId(Long businessId);
    
    Long countByBusinessIdAndIsAvailableTrue(Long businessId);
    
    // ========== ORIGINAL QUERIES ==========
    
    List<RestaurantTable> findByIsAvailableTrue();
    
    Optional<RestaurantTable> findByTableNumber(String tableNumber);
    
    List<RestaurantTable> findByLocation(String location);
}
