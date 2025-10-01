package com.vijay.User_Master.repository.hotel;

import com.vijay.User_Master.entity.hotel.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Long> {
    
    List<RestaurantTable> findByIsAvailableTrue();
    
    Optional<RestaurantTable> findByTableNumber(String tableNumber);
    
    List<RestaurantTable> findByLocation(String location);
}
