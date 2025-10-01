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
    
    // ========== MULTI-TENANT QUERIES ==========
    
    // Find by business
    List<MenuItem> findByBusinessId(Long businessId);
    
    List<MenuItem> findByBusinessIdAndIsAvailableTrue(Long businessId);
    
    Page<MenuItem> findByBusinessId(Long businessId, Pageable pageable);
    
    // Find by business and category
    List<MenuItem> findByBusinessIdAndCategoryIdAndIsAvailableTrue(Long businessId, Long categoryId);
    
    @Query("SELECT m FROM MenuItem m WHERE m.businessId = :businessId AND m.category.id = :categoryId AND m.isAvailable = true")
    Page<MenuItem> findByBusinessIdAndCategoryId(@Param("businessId") Long businessId, 
                                                   @Param("categoryId") Long categoryId, 
                                                   Pageable pageable);
    
    // Find featured items by business
    List<MenuItem> findByBusinessIdAndIsFeaturedTrueAndIsAvailableTrue(Long businessId);
    
    // Find vegetarian items by business
    List<MenuItem> findByBusinessIdAndIsVegetarianTrueAndIsAvailableTrue(Long businessId);
    
    // Search within business
    @Query("SELECT m FROM MenuItem m WHERE m.businessId = :businessId AND m.isAvailable = true AND " +
           "(LOWER(m.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(m.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<MenuItem> searchMenuItemsByBusinessId(@Param("businessId") Long businessId, 
                                                @Param("keyword") String keyword, 
                                                Pageable pageable);
    
    // Count by business
    Long countByBusinessId(Long businessId);
    
    Long countByBusinessIdAndIsAvailableTrue(Long businessId);
    
    // ========== ORIGINAL QUERIES (Keep for backward compatibility) ==========
    
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
