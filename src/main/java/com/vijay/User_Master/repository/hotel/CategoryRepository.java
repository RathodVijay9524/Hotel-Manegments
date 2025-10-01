package com.vijay.User_Master.repository.hotel;

import com.vijay.User_Master.entity.hotel.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    // ========== MULTI-TENANT QUERIES ==========
    
    List<Category> findByBusinessId(Long businessId);
    
    List<Category> findByBusinessIdAndIsActiveTrueOrderByDisplayOrderAsc(Long businessId);
    
    List<Category> findByBusinessIdOrderByDisplayOrderAsc(Long businessId);
    
    Long countByBusinessId(Long businessId);
    
    Long countByBusinessIdAndIsActiveTrue(Long businessId);
    
    // ========== ORIGINAL QUERIES ==========
    
    List<Category> findByIsActiveTrueOrderByDisplayOrderAsc();
    
    List<Category> findAllByOrderByDisplayOrderAsc();
}
