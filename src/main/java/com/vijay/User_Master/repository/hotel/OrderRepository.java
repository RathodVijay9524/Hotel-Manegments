package com.vijay.User_Master.repository.hotel;

import com.vijay.User_Master.entity.hotel.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    // ========== MULTI-TENANT QUERIES ==========
    
    // Find by business ID
    List<Order> findByBusinessId(Long businessId);
    
    Page<Order> findByBusinessId(Long businessId, Pageable pageable);
    
    // Find by business and status
    List<Order> findByBusinessIdAndStatus(Long businessId, Order.OrderStatus status);
    
    Page<Order> findByBusinessIdAndStatus(Long businessId, Order.OrderStatus status, Pageable pageable);
    
    // Find by business and user
    List<Order> findByBusinessIdAndUserId(Long businessId, Long userId);
    
    Page<Order> findByBusinessIdAndUserId(Long businessId, Long userId, Pageable pageable);
    
    // Count by business
    Long countByBusinessId(Long businessId);
    
    // Count by business and status
    Long countByBusinessIdAndStatus(Long businessId, Order.OrderStatus status);
    
    // Find by business and date range
    @Query("SELECT o FROM Order o WHERE o.businessId = :businessId AND o.createdAt BETWEEN :startDate AND :endDate")
    List<Order> findByBusinessIdAndDateRange(@Param("businessId") Long businessId,
                                              @Param("startDate") LocalDateTime startDate,
                                              @Param("endDate") LocalDateTime endDate);
    
    // Today's orders by business
    @Query("SELECT o FROM Order o WHERE o.businessId = :businessId AND DATE(o.createdAt) = CURRENT_DATE")
    List<Order> findTodaysOrdersByBusinessId(@Param("businessId") Long businessId);
    
    // Active orders by business and table
    @Query("SELECT o FROM Order o WHERE o.businessId = :businessId AND o.table.id = :tableId AND o.status NOT IN ('COMPLETED', 'CANCELLED')")
    List<Order> findActiveOrdersByBusinessIdAndTable(@Param("businessId") Long businessId, @Param("tableId") Long tableId);
    
    // Total revenue by business
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.businessId = :businessId")
    Double getTotalRevenueByBusinessId(@Param("businessId") Long businessId);
    
    // Revenue by business and date range
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.businessId = :businessId AND o.createdAt BETWEEN :startDate AND :endDate")
    Double getRevenueByBusinessIdAndDateRange(@Param("businessId") Long businessId,
                                               @Param("startDate") LocalDateTime startDate,
                                               @Param("endDate") LocalDateTime endDate);
    
    // ========== ORIGINAL QUERIES (Keep for backward compatibility) ==========
    
    Optional<Order> findByOrderNumber(String orderNumber);
    
    List<Order> findByUserId(Long userId);
    
    Page<Order> findByUserId(Long userId, Pageable pageable);
    
    List<Order> findByStatus(Order.OrderStatus status);
    
    Page<Order> findByStatus(Order.OrderStatus status, Pageable pageable);
    
    @Query("SELECT o FROM Order o WHERE o.userId = :userId AND o.status = :status")
    List<Order> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Order.OrderStatus status);
    
    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate")
    List<Order> findOrdersBetweenDates(@Param("startDate") LocalDateTime startDate, 
                                       @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT o FROM Order o WHERE o.table.id = :tableId AND o.status NOT IN ('COMPLETED', 'CANCELLED')")
    List<Order> findActiveOrdersByTable(@Param("tableId") Long tableId);
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    Long countByStatus(@Param("status") Order.OrderStatus status);
    
    List<Order> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    Long countByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
}
