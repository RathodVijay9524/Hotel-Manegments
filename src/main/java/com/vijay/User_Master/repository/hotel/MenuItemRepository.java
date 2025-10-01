package com.vijay.User_Master.repository.hotel;

import com.vijay.User_Master.entity.hotel.MenuItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    
    List<MenuItem> findByCategoryIdAndIsAvailableTrue(Long categoryId);
    
    List<MenuItem> findByIsAvailableTrue();
    
    List<MenuItem> findByIsFeaturedTrueAndIsAvailableTrue();
    
    List<MenuItem> findByIsVegetarianTrueAndIsAvailableTrue();
    
    @Query("SELECT m FROM MenuItem m WHERE m.isAvailable = true AND " +
           "(LOWER(m.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(m.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<MenuItem> searchMenuItems(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT m FROM MenuItem m WHERE m.category.id = :categoryId AND m.isAvailable = true")
    Page<MenuItem> findByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);
}
