package com.vijay.User_Master.service.hotel;

import com.vijay.User_Master.dto.hotel.CreateReviewRequest;
import com.vijay.User_Master.dto.hotel.MenuItemWithReviewsDTO;
import com.vijay.User_Master.dto.hotel.ReviewDTO;
import com.vijay.User_Master.entity.hotel.MenuItem;
import com.vijay.User_Master.entity.hotel.Order;
import com.vijay.User_Master.entity.hotel.Review;
import com.vijay.User_Master.repository.hotel.MenuItemRepository;
import com.vijay.User_Master.repository.hotel.OrderRepository;
import com.vijay.User_Master.repository.hotel.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {
    
    private final ReviewRepository reviewRepository;
    private final MenuItemRepository menuItemRepository;
    private final OrderRepository orderRepository;
    
    @Transactional
    public ReviewDTO createReview(CreateReviewRequest request) {
        log.info("Creating review for menu item: {} by user: {}", request.getMenuItemId(), request.getUserId());
        
        // Validate rating (1-5)
        if (request.getRating() < 1 || request.getRating() > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }
        
        // Check if user already reviewed this item
        reviewRepository.findByMenuItemIdAndUserId(request.getMenuItemId(), request.getUserId())
                .ifPresent(r -> {
                    throw new RuntimeException("You have already reviewed this item. Please update your existing review.");
                });
        
        MenuItem menuItem = menuItemRepository.findById(request.getMenuItemId())
                .orElseThrow(() -> new RuntimeException("Menu item not found"));
        
        // Check if user has ordered this item (verified purchase)
        boolean isVerifiedPurchase = hasUserOrderedItem(request.getUserId(), request.getMenuItemId());
        
        Review review = Review.builder()
                .menuItem(menuItem)
                .userId(request.getUserId())
                .userName(request.getUserName())
                .rating(request.getRating())
                .comment(request.getComment())
                .isVerifiedPurchase(isVerifiedPurchase)
                .isApproved(true) // Auto-approve for now
                .helpfulCount(0)
                .isEdited(false)
                .build();
        
        review = reviewRepository.save(review);
        
        // Update menu item rating
        updateMenuItemRating(request.getMenuItemId());
        
        log.info("Review created successfully with ID: {}", review.getId());
        return mapToReviewDTO(review);
    }
    
    @Transactional
    public ReviewDTO updateReview(Long reviewId, CreateReviewRequest request) {
        log.info("Updating review: {}", reviewId);
        
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        
        // Verify user owns this review
        if (!review.getUserId().equals(request.getUserId())) {
            throw new RuntimeException("You can only update your own reviews");
        }
        
        // Validate rating
        if (request.getRating() < 1 || request.getRating() > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }
        
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setIsEdited(true);
        review.setEditedAt(LocalDateTime.now());
        
        review = reviewRepository.save(review);
        
        // Update menu item rating
        updateMenuItemRating(review.getMenuItem().getId());
        
        return mapToReviewDTO(review);
    }
    
    @Transactional
    public void deleteReview(Long reviewId, Long userId) {
        log.info("Deleting review: {} by user: {}", reviewId, userId);
        
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        
        // Verify user owns this review
        if (!review.getUserId().equals(userId)) {
            throw new RuntimeException("You can only delete your own reviews");
        }
        
        Long menuItemId = review.getMenuItem().getId();
        reviewRepository.delete(review);
        
        // Update menu item rating
        updateMenuItemRating(menuItemId);
    }
    
    public ReviewDTO getReviewById(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        return mapToReviewDTO(review);
    }
    
    public List<ReviewDTO> getReviewsByMenuItem(Long menuItemId) {
        return reviewRepository.findByMenuItemIdAndIsApprovedTrue(menuItemId).stream()
                .map(this::mapToReviewDTO)
                .collect(Collectors.toList());
    }
    
    public Page<ReviewDTO> getReviewsByMenuItem(Long menuItemId, Pageable pageable) {
        return reviewRepository.findByMenuItemIdAndIsApprovedTrue(menuItemId, pageable)
                .map(this::mapToReviewDTO);
    }
    
    public List<ReviewDTO> getReviewsByUser(Long userId) {
        return reviewRepository.findByUserId(userId).stream()
                .map(this::mapToReviewDTO)
                .collect(Collectors.toList());
    }
    
    public Page<ReviewDTO> getReviewsByUser(Long userId, Pageable pageable) {
        return reviewRepository.findByUserId(userId, pageable)
                .map(this::mapToReviewDTO);
    }
    
    public List<ReviewDTO> getReviewsByRating(Long menuItemId, Integer rating) {
        return reviewRepository.findByMenuItemIdAndRatingAndIsApprovedTrue(menuItemId, rating).stream()
                .map(this::mapToReviewDTO)
                .collect(Collectors.toList());
    }
    
    public List<ReviewDTO> getVerifiedPurchaseReviews(Long menuItemId) {
        return reviewRepository.findByMenuItemIdAndIsVerifiedPurchaseTrueAndIsApprovedTrue(menuItemId).stream()
                .map(this::mapToReviewDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public ReviewDTO markReviewHelpful(Long reviewId) {
        log.info("Marking review {} as helpful", reviewId);
        
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        
        review.setHelpfulCount(review.getHelpfulCount() + 1);
        review = reviewRepository.save(review);
        
        return mapToReviewDTO(review);
    }
    
    public MenuItemWithReviewsDTO getMenuItemWithReviews(Long menuItemId) {
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new RuntimeException("Menu item not found"));
        
        List<Review> reviews = reviewRepository.findByMenuItemIdAndIsApprovedTrue(menuItemId);
        
        // Calculate statistics
        Long totalReviews = (long) reviews.size();
        Double avgRating = reviewRepository.calculateAverageRating(menuItemId);
        
        int fiveStar = (int) reviews.stream().filter(r -> r.getRating() == 5).count();
        int fourStar = (int) reviews.stream().filter(r -> r.getRating() == 4).count();
        int threeStar = (int) reviews.stream().filter(r -> r.getRating() == 3).count();
        int twoStar = (int) reviews.stream().filter(r -> r.getRating() == 2).count();
        int oneStar = (int) reviews.stream().filter(r -> r.getRating() == 1).count();
        
        // Get recent reviews (top 5)
        List<ReviewDTO> recentReviews = reviews.stream()
                .sorted((r1, r2) -> r2.getCreatedAt().compareTo(r1.getCreatedAt()))
                .limit(5)
                .map(this::mapToReviewDTO)
                .collect(Collectors.toList());
        
        return MenuItemWithReviewsDTO.builder()
                .id(menuItem.getId())
                .name(menuItem.getName())
                .description(menuItem.getDescription())
                .price(menuItem.getPrice())
                .imageUrl(menuItem.getImageUrl())
                .isVegetarian(menuItem.getIsVegetarian())
                .isSpicy(menuItem.getIsSpicy())
                .preparationTime(menuItem.getPreparationTime())
                .averageRating(avgRating != null ? BigDecimal.valueOf(avgRating).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO)
                .totalReviews(totalReviews)
                .fiveStarCount(fiveStar)
                .fourStarCount(fourStar)
                .threeStarCount(threeStar)
                .twoStarCount(twoStar)
                .oneStarCount(oneStar)
                .recentReviews(recentReviews)
                .build();
    }
    
    // ==================== HELPER METHODS ====================
    
    private void updateMenuItemRating(Long menuItemId) {
        Double avgRating = reviewRepository.calculateAverageRating(menuItemId);
        
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new RuntimeException("Menu item not found"));
        
        if (avgRating != null) {
            menuItem.setRating(BigDecimal.valueOf(avgRating).setScale(2, RoundingMode.HALF_UP));
        } else {
            menuItem.setRating(BigDecimal.ZERO);
        }
        
        menuItemRepository.save(menuItem);
    }
    
    private boolean hasUserOrderedItem(Long userId, Long menuItemId) {
        List<Order> userOrders = orderRepository.findByUserId(userId);
        
        return userOrders.stream()
                .anyMatch(order -> order.getItems().stream()
                        .anyMatch(item -> item.getMenuItem().getId().equals(menuItemId)));
    }
    
    private ReviewDTO mapToReviewDTO(Review review) {
        return ReviewDTO.builder()
                .id(review.getId())
                .menuItemId(review.getMenuItem().getId())
                .menuItemName(review.getMenuItem().getName())
                .userId(review.getUserId())
                .userName(review.getUserName())
                .rating(review.getRating())
                .comment(review.getComment())
                .isVerifiedPurchase(review.getIsVerifiedPurchase())
                .isApproved(review.getIsApproved())
                .helpfulCount(review.getHelpfulCount())
                .isEdited(review.getIsEdited())
                .editedAt(review.getEditedAt())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
