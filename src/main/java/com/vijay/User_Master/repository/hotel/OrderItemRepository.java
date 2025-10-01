package com.vijay.User_Master.repository.hotel;

import com.vijay.User_Master.entity.hotel.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
    List<OrderItem> findByOrderId(Long orderId);
    
    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.id = :orderId AND oi.status = :status")
    List<OrderItem> findByOrderIdAndStatus(@Param("orderId") Long orderId, 
                                           @Param("status") OrderItem.ItemStatus status);
}
