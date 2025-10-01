package com.vijay.User_Master.repository.hotel;

import com.vijay.User_Master.entity.hotel.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    // Get all reviews for a menu item
    List<Review> findByMenuItemIdAndIsApprovedTrue(Long menuItemId);
    
    Page<Review> findByMenuItemIdAndIsApprovedTrue(Long menuItemId, Pageable pageable);
    
    // Get reviews by user
    List<Review> findByUserId(Long userId);
    
    Page<Review> findByUserId(Long userId, Pageable pageable);
    
    // Check if user already reviewed this item
    Optional<Review> findByMenuItemIdAndUserId(Long menuItemId, Long userId);
    
    // Get reviews by rating
    List<Review> findByMenuItemIdAndRatingAndIsApprovedTrue(Long menuItemId, Integer rating);
    
    // Get verified purchase reviews
    List<Review> findByMenuItemIdAndIsVerifiedPurchaseTrueAndIsApprovedTrue(Long menuItemId);
    
    // Count reviews for a menu item
    Long countByMenuItemIdAndIsApprovedTrue(Long menuItemId);
    
    // Calculate average rating
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.menuItem.id = :menuItemId AND r.isApproved = true")
    Double calculateAverageRating(@Param("menuItemId") Long menuItemId);
    
    // Get reviews pending approval
    List<Review> findByIsApprovedFalse();
    
    // Get top rated items
    @Query("SELECT r.menuItem.id, AVG(r.rating) as avgRating FROM Review r WHERE r.isApproved = true GROUP BY r.menuItem.id ORDER BY avgRating DESC")
    List<Object[]> findTopRatedItems();
}
