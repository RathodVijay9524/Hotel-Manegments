package com.vijay.User_Master.controller.hotel;

import com.vijay.User_Master.dto.hotel.CreateReviewRequest;
import com.vijay.User_Master.dto.hotel.MenuItemWithReviewsDTO;
import com.vijay.User_Master.dto.hotel.ReviewDTO;
import com.vijay.User_Master.service.hotel.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hotel/reviews")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReviewController {
    
    private final ReviewService reviewService;
    
    // Create a new review
    @PostMapping
    public ResponseEntity<ReviewDTO> createReview(@RequestBody CreateReviewRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.createReview(request));
    }
    
    // Update existing review
    @PutMapping("/{id}")
    public ResponseEntity<ReviewDTO> updateReview(
            @PathVariable Long id,
            @RequestBody CreateReviewRequest request) {
        return ResponseEntity.ok(reviewService.updateReview(id, request));
    }
    
    // Delete review
    @DeleteMapping("/{id}/user/{userId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long id,
            @PathVariable Long userId) {
        reviewService.deleteReview(id, userId);
        return ResponseEntity.noContent().build();
    }
    
    // Get review by ID
    @GetMapping("/{id}")
    public ResponseEntity<ReviewDTO> getReviewById(@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.getReviewById(id));
    }
    
    // Get all reviews for a menu item
    @GetMapping("/menu-item/{menuItemId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByMenuItem(@PathVariable Long menuItemId) {
        return ResponseEntity.ok(reviewService.getReviewsByMenuItem(menuItemId));
    }
    
    // Get reviews for menu item with pagination
    @GetMapping("/menu-item/{menuItemId}/pageable")
    public ResponseEntity<Page<ReviewDTO>> getReviewsByMenuItemPageable(
            @PathVariable Long menuItemId,
            Pageable pageable) {
        return ResponseEntity.ok(reviewService.getReviewsByMenuItem(menuItemId, pageable));
    }
    
    // Get all reviews by a user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(reviewService.getReviewsByUser(userId));
    }
    
    // Get user reviews with pagination
    @GetMapping("/user/{userId}/pageable")
    public ResponseEntity<Page<ReviewDTO>> getReviewsByUserPageable(
            @PathVariable Long userId,
            Pageable pageable) {
        return ResponseEntity.ok(reviewService.getReviewsByUser(userId, pageable));
    }
    
    // Get reviews by rating
    @GetMapping("/menu-item/{menuItemId}/rating/{rating}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByRating(
            @PathVariable Long menuItemId,
            @PathVariable Integer rating) {
        return ResponseEntity.ok(reviewService.getReviewsByRating(menuItemId, rating));
    }
    
    // Get verified purchase reviews only
    @GetMapping("/menu-item/{menuItemId}/verified")
    public ResponseEntity<List<ReviewDTO>> getVerifiedPurchaseReviews(@PathVariable Long menuItemId) {
        return ResponseEntity.ok(reviewService.getVerifiedPurchaseReviews(menuItemId));
    }
    
    // Mark review as helpful
    @PostMapping("/{id}/helpful")
    public ResponseEntity<ReviewDTO> markReviewHelpful(@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.markReviewHelpful(id));
    }
    
    // Get menu item with all review statistics
    @GetMapping("/menu-item/{menuItemId}/details")
    public ResponseEntity<MenuItemWithReviewsDTO> getMenuItemWithReviews(@PathVariable Long menuItemId) {
        return ResponseEntity.ok(reviewService.getMenuItemWithReviews(menuItemId));
    }
}
