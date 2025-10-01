package com.vijay.User_Master.Helper;

import com.vijay.User_Master.config.security.CustomUserDetails;
import com.vijay.User_Master.entity.Role;
import com.vijay.User_Master.entity.User;
import com.vijay.User_Master.entity.Worker;
import com.vijay.User_Master.repository.UserRepository;
import com.vijay.User_Master.repository.WorkerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Business Context Filter - Determines the business ID based on user role
 * 
 * Role-Based Business ID Logic:
 * - ADMIN: Returns null (sees all businesses)
 * - OWNER: Returns user.id (their business ID)
 * - WORKER/MANAGER: Returns their owner's user.id (from worker.user relationship)
 * - NORMAL: Returns null (customer)
 * 
 * Uses existing CommonUtils.getLoggedInUser() method
 */
@Component
@Slf4j
public class BusinessContextFilter {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private WorkerRepository workerRepository;
    
    /**
     * Get the current logged-in user's business ID based on their role
     * Uses existing CommonUtils.getLoggedInUser() method
     * 
     * @return Business ID (null for ADMIN, user.id for OWNER, owner's ID for WORKER)
     */
    public Long getCurrentBusinessId() {
        try {
            CustomUserDetails loggedInUser = CommonUtils.getLoggedInUser();
            return getBusinessIdForCustomUserDetails(loggedInUser);
        } catch (Exception e) {
            log.error("Error getting business ID for current user", e);
            return null;
        }
    }
    
    /**
     * Get business ID from CustomUserDetails
     * 
     * @param userDetails CustomUserDetails object
     * @return Business ID
     */
    private Long getBusinessIdForCustomUserDetails(CustomUserDetails userDetails) {
        if (userDetails == null) {
            log.warn("UserDetails is null, returning null business ID");
            return null;
        }
        
        Set<Role> roles = userDetails.getRoles();
        
        // Check if user is ADMIN - they see all businesses
        if (hasRole(roles, "ROLE_ADMIN")) {
            log.debug("User {} has ADMIN role - accessing all businesses", userDetails.getUsername());
            return null; // null means "all businesses"
        }
        
        // Check if user is OWNER - their ID is the business ID
        if (hasRole(roles, "ROLE_OWNER")) {
            log.debug("User {} has OWNER role - business ID: {}", userDetails.getUsername(), userDetails.getId());
            return userDetails.getId(); // User ID = Business ID for owners
        }
        
        // For WORKER/MANAGER - get the owner's ID from worker.user relationship
        if (hasRole(roles, "ROLE_WORKER") || hasRole(roles, "ROLE_MANAGER")) {
            Worker worker = workerRepository.findById(userDetails.getId()).orElse(null);
            if (worker != null && worker.getUser() != null) {
                Long ownerId = worker.getUser().getId();
                log.debug("Worker {} belongs to business ID: {}", userDetails.getUsername(), ownerId);
                return ownerId; // Return the owner's ID as business ID
            }
            log.warn("Worker {} has no associated owner", userDetails.getUsername());
            return null;
        }
        
        // NORMAL users (customers) don't have business access
        log.debug("User {} has NORMAL role - no business access", userDetails.getUsername());
        return null;
    }
    
    /**
     * Get business ID for a specific user
     * 
     * @param user User entity
     * @return Business ID (null for ADMIN)
     */
    public Long getBusinessIdForUser(User user) {
        if (user == null) {
            log.warn("User is null, returning null business ID");
            return null;
        }
        
        Set<Role> roles = user.getRoles();
        
        // Check if user is ADMIN - they see all businesses
        if (hasRole(roles, "ROLE_ADMIN")) {
            log.debug("User {} has ADMIN role - accessing all businesses", user.getUsername());
            return null; // null means "all businesses"
        }
        
        // Check if user is OWNER - their ID is the business ID
        if (hasRole(roles, "ROLE_OWNER")) {
            log.debug("User {} has OWNER role - business ID: {}", user.getUsername(), user.getId());
            return user.getId(); // User ID = Business ID for owners
        }
        
        // For MANAGER/WORKER - they would have a business_id field
        // (You can add this field to Worker entity if needed)
        if (hasRole(roles, "ROLE_MANAGER") || hasRole(roles, "ROLE_WORKER")) {
            log.warn("MANAGER/WORKER role detected but business ID mapping not implemented yet");
            // TODO: Implement worker-to-business mapping
            return null;
        }
        
        // NORMAL users (customers) don't have business access
        log.debug("User {} has NORMAL role - no business access", user.getUsername());
        return null;
    }
    
    /**
     * Check if user has a specific role
     * 
     * @param roles Set of user roles
     * @param roleName Role name to check (e.g., "ROLE_ADMIN")
     * @return true if user has the role
     */
    private boolean hasRole(Set<Role> roles, String roleName) {
        if (roles == null || roles.isEmpty()) {
            return false;
        }
        
        return roles.stream()
                .anyMatch(role -> roleName.equals(role.getName()));
    }
    
    /**
     * Check if current logged-in user is ADMIN
     * 
     * @return true if user is ADMIN
     */
    public boolean isAdmin() {
        try {
            CustomUserDetails loggedInUser = CommonUtils.getLoggedInUser();
            return hasRole(loggedInUser.getRoles(), "ROLE_ADMIN");
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Check if current logged-in user is OWNER
     * 
     * @return true if user is OWNER
     */
    public boolean isOwner() {
        try {
            CustomUserDetails loggedInUser = CommonUtils.getLoggedInUser();
            return hasRole(loggedInUser.getRoles(), "ROLE_OWNER");
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Check if current logged-in user is WORKER or MANAGER
     * 
     * @return true if user is WORKER or MANAGER
     */
    public boolean isWorkerOrManager() {
        try {
            CustomUserDetails loggedInUser = CommonUtils.getLoggedInUser();
            return hasRole(loggedInUser.getRoles(), "ROLE_WORKER") || 
                   hasRole(loggedInUser.getRoles(), "ROLE_MANAGER");
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Get the current logged-in CustomUserDetails
     * 
     * @return CustomUserDetails object
     */
    public CustomUserDetails getCurrentUserDetails() {
        return CommonUtils.getLoggedInUser();
    }
    
    /**
     * Get the current logged-in user's ID
     * 
     * @return User ID
     */
    public Long getCurrentUserId() {
        try {
            CustomUserDetails loggedInUser = CommonUtils.getLoggedInUser();
            return loggedInUser.getId();
        } catch (Exception e) {
            throw new RuntimeException("User not authenticated");
        }
    }
    
    /**
     * Validate that a resource belongs to the current user's business
     * Throws exception if access is denied
     * 
     * @param resourceBusinessId Business ID of the resource being accessed
     * @throws RuntimeException if access is denied
     */
    public void validateBusinessAccess(Long resourceBusinessId) {
        // Admin can access everything
        if (isAdmin()) {
            return;
        }
        
        Long userBusinessId = getCurrentBusinessId();
        
        // If user has no business ID, deny access
        if (userBusinessId == null) {
            log.warn("Access denied - User has no business context");
            throw new RuntimeException("Access denied - You don't have permission to access this resource");
        }
        
        // Check if resource belongs to user's business
        if (!userBusinessId.equals(resourceBusinessId)) {
            log.warn("Access denied - Resource business_id {} doesn't match user business_id {}", 
                    resourceBusinessId, userBusinessId);
            throw new RuntimeException("Access denied - This resource belongs to a different business");
        }
    }
}
