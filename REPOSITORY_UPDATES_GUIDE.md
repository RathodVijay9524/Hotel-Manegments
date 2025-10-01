# 📝 REPOSITORY UPDATES GUIDE - Business ID Methods

**Status:** Templates for remaining repositories  
**Purpose:** Add business_id filtering to all repositories

---

## ✅ COMPLETED

- [x] OrderRepository - Full multi-tenant queries added
- [x] MenuItemRepository - Full multi-tenant queries added

---

## ⏳ REMAINING REPOSITORIES TO UPDATE

### **1. CategoryRepository**
```java
// Add these methods:
List<Category> findByBusinessId(Long businessId);
List<Category> findByBusinessIdAndIsActiveTrue(Long businessId);
Page<Category> findByBusinessId(Long businessId, Pageable pageable);
Long countByBusinessId(Long businessId);
```

### **2. RestaurantTableRepository**
```java
// Add these methods:
List<RestaurantTable> findByBusinessId(Long businessId);
List<RestaurantTable> findByBusinessIdAndStatus(Long businessId, RestaurantTable.TableStatus status);
List<RestaurantTable> findByBusinessIdAndStatusAndCapacityGreaterThanEqual(
    Long businessId, RestaurantTable.TableStatus status, Integer capacity);
Long countByBusinessId(Long businessId);
Long countByBusinessIdAndStatus(Long businessId, RestaurantTable.TableStatus status);
```

### **3. TableReservationRepository**
```java
// Add these methods:
List<TableReservation> findByBusinessId(Long businessId);
List<TableReservation> findByBusinessIdAndStatus(Long businessId, TableReservation.ReservationStatus status);
List<TableReservation> findByBusinessIdAndReservationDateBetween(
    Long businessId, LocalDateTime start, LocalDateTime end);
Long countByBusinessId(Long businessId);
Long countByBusinessIdAndStatus(Long businessId, TableReservation.ReservationStatus status);
```

### **4. DeliveryAgentRepository**
```java
// Add these methods:
List<DeliveryAgent> findByBusinessId(Long businessId);
List<DeliveryAgent> findByBusinessIdAndIsAvailableTrueAndIsOnlineTrue(Long businessId);
Long countByBusinessId(Long businessId);
```

### **5. DeliveryTrackingRepository**
```java
// Add these methods:
List<DeliveryTracking> findByBusinessId(Long businessId);
List<DeliveryTracking> findByBusinessIdAndStatus(Long businessId, DeliveryTracking.DeliveryStatus status);
Long countByBusinessId(Long businessId);
```

### **6. PaymentRepository**
```java
// Add these methods:
List<Payment> findByBusinessId(Long businessId);
Page<Payment> findByBusinessId(Long businessId, Pageable pageable);
Long countByBusinessId(Long businessId);

@Query("SELECT SUM(p.amount) FROM Payment p WHERE p.businessId = :businessId")
Double getTotalPaymentsByBusinessId(@Param("businessId") Long businessId);
```

### **7. ReviewRepository**
```java
// Add these methods:
List<Review> findByBusinessId(Long businessId);
List<Review> findByBusinessIdAndMenuItemId(Long businessId, Long menuItemId);
Page<Review> findByBusinessId(Long businessId, Pageable pageable);
Long countByBusinessId(Long businessId);

@Query("SELECT AVG(r.rating) FROM Review r WHERE r.businessId = :businessId")
Double getAverageRatingByBusinessId(@Param("businessId") Long businessId);
```

---

## 🚀 QUICK UPDATE PATTERN

For each repository, add this section at the top after the interface declaration:

```java
@Repository
public interface XxxRepository extends JpaRepository<Xxx, Long> {
    
    // ========== MULTI-TENANT QUERIES ==========
    
    // Find by business ID
    List<Xxx> findByBusinessId(Long businessId);
    
    Page<Xxx> findByBusinessId(Long businessId, Pageable pageable);
    
    // Count by business
    Long countByBusinessId(Long businessId);
    
    // Add specific business-filtered methods here
    
    // ========== ORIGINAL QUERIES ==========
    
    // Keep all existing methods below
}
```

---

## 💡 IMPLEMENTATION PRIORITY

**High Priority** (Needed immediately):
1. ✅ OrderRepository
2. ✅ MenuItemRepository
3. CategoryRepository
4. RestaurantTableRepository
5. TableReservationRepository

**Medium Priority** (Can wait):
6. PaymentRepository
7. ReviewRepository

**Low Priority** (Less critical):
8. DeliveryAgentRepository
9. DeliveryTrackingRepository

---

## ⚡ NEXT STEP

After repositories are done, update services to USE these methods!
