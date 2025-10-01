# 🎯 BUSINESS CONTEXT FILTER - USAGE GUIDE

**Date:** October 1, 2025  
**Component:** BusinessContextFilter.java

---

## 📋 OVERVIEW

The `BusinessContextFilter` utility provides business ID filtering based on user roles, using your existing `CommonUtils.getLoggedInUser()` method.

---

## 🔍 HOW IT WORKS

### **Role-Based Business ID Logic:**

| Role | Business ID Returned | Access Level |
|------|---------------------|--------------|
| **ADMIN** | `null` | Sees ALL hotels |
| **OWNER** | `user.id` | Sees THEIR hotel only |
| **WORKER/MANAGER** | `owner.id` (from `worker.user`) | Sees owner's hotel |
| **NORMAL** | `null` | No business access (customer) |

### **Architecture Flow:**

```
User Login → CommonUtils.getLoggedInUser() → Check Role
    ↓
ADMIN     → business_id = null      → Query: SELECT * FROM orders
OWNER     → business_id = user.id   → Query: SELECT * FROM orders WHERE business_id = 1
WORKER    → business_id = owner.id  → Query: SELECT * FROM orders WHERE business_id = 1
NORMAL    → business_id = null      → No access
```

---

## 🚀 AVAILABLE METHODS

### **1. Get Current Business ID**
```java
@Autowired
private BusinessContextFilter businessContext;

public List<Order> getOrders() {
    Long businessId = businessContext.getCurrentBusinessId();
    
    if (businessId == null) {
        // ADMIN - return all
        return orderRepository.findAll();
    } else {
        // OWNER/WORKER - filter by business
        return orderRepository.findByBusinessId(businessId);
    }
}
```

### **2. Check User Role**
```java
// Check if ADMIN
if (businessContext.isAdmin()) {
    // Admin-only logic
}

// Check if OWNER
if (businessContext.isOwner()) {
    // Owner-only logic
}

// Check if WORKER/MANAGER
if (businessContext.isWorkerOrManager()) {
    // Worker/Manager logic
}
```

### **3. Get Current User Details**
```java
CustomUserDetails currentUser = businessContext.getCurrentUserDetails();
Long userId = businessContext.getCurrentUserId();
```

### **4. Validate Business Access**
```java
// Throws exception if access denied
public Order getOrder(Long orderId) {
    Order order = orderRepository.findById(orderId).orElseThrow();
    
    // Validate user has access to this order's business
    businessContext.validateBusinessAccess(order.getBusinessId());
    
    return order;
}
```

---

## 💼 USAGE IN SERVICES

### **Example: OrderService**

```java
@Service
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private BusinessContextFilter businessContext;
    
    /**
     * Get all orders for current user's business
     */
    public List<Order> getAllOrders() {
        Long businessId = businessContext.getCurrentBusinessId();
        
        if (businessId == null) {
            // ADMIN sees all
            return orderRepository.findAll();
        }
        
        // OWNER/WORKER sees their business only
        return orderRepository.findByBusinessId(businessId);
    }
    
    /**
     * Create new order (auto-assign business ID)
     */
    public Order createOrder(CreateOrderRequest request) {
        Long businessId = businessContext.getCurrentBusinessId();
        
        if (businessId == null) {
            throw new RuntimeException("Cannot create order without business context");
        }
        
        Order order = Order.builder()
                .businessId(businessId) // Auto-assign
                .orderNumber(generateOrderNumber())
                .customerName(request.getCustomerName())
                // ... other fields
                .build();
        
        return orderRepository.save(order);
    }
    
    /**
     * Get order by ID (with access validation)
     */
    public Order getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        // Validate access
        businessContext.validateBusinessAccess(order.getBusinessId());
        
        return order;
    }
    
    /**
     * Update order (with access validation)
     */
    public Order updateOrder(Long orderId, UpdateOrderRequest request) {
        Order order = getOrderById(orderId); // Already validates access
        
        // Update fields
        order.setStatus(request.getStatus());
        // ... other updates
        
        return orderRepository.save(order);
    }
}
```

---

## 📊 USAGE IN REPOSITORIES

### **Add business_id filter methods:**

```java
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    // Find by business ID
    List<Order> findByBusinessId(Long businessId);
    
    // Find by user and business
    List<Order> findByUserIdAndBusinessId(Long userId, Long businessId);
    
    // Find today's orders by business
    @Query("SELECT o FROM Order o WHERE o.businessId = :businessId " +
           "AND DATE(o.createdAt) = CURRENT_DATE")
    List<Order> findTodaysOrdersByBusinessId(@Param("businessId") Long businessId);
    
    // Count by business
    Long countByBusinessId(Long businessId);
    
    // Sum revenue by business
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.businessId = :businessId")
    BigDecimal getTotalRevenueByBusinessId(@Param("businessId") Long businessId);
}
```

---

## 🎯 USAGE IN CONTROLLERS

### **Example: OrderController**

```java
@RestController
@RequestMapping("/api/hotel/orders")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private BusinessContextFilter businessContext;
    
    /**
     * Get all orders (filtered by business)
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER', 'MANAGER', 'WORKER')")
    public ResponseEntity<List<Order>> getAllOrders() {
        // BusinessContext automatically filters
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }
    
    /**
     * Create order (business ID auto-assigned)
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'WORKER')")
    public ResponseEntity<Order> createOrder(@RequestBody CreateOrderRequest request) {
        // BusinessContext assigns current business ID
        Order order = orderService.createOrder(request);
        return ResponseEntity.ok(order);
    }
    
    /**
     * Get order by ID (access validated)
     */
    @GetMapping("/{orderId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER', 'MANAGER', 'WORKER')")
    public ResponseEntity<Order> getOrder(@PathVariable Long orderId) {
        // Validates user has access to this order's business
        Order order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(order);
    }
}
```

---

## 📈 ANALYTICS USAGE

### **Admin Dashboard (All Hotels)**

```java
@GetMapping("/admin/dashboard")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<DashboardDTO> getAdminDashboard() {
    // No business filter - sees all
    List<Order> allOrders = orderRepository.findAll();
    List<User> allOwners = userRepository.findByRolesName("ROLE_OWNER");
    
    DashboardDTO dashboard = DashboardDTO.builder()
            .totalOrders(allOrders.size())
            .totalHotels(allOwners.size())
            .totalRevenue(calculateTotalRevenue(allOrders))
            .build();
    
    return ResponseEntity.ok(dashboard);
}
```

### **Owner Dashboard (Single Hotel)**

```java
@GetMapping("/owner/dashboard")
@PreAuthorize("hasRole('OWNER')")
public ResponseEntity<DashboardDTO> getOwnerDashboard() {
    Long businessId = businessContext.getCurrentBusinessId();
    
    // Filter by business ID
    List<Order> myOrders = orderRepository.findByBusinessId(businessId);
    List<Worker> myWorkers = workerRepository.findByUserId(businessId);
    
    DashboardDTO dashboard = DashboardDTO.builder()
            .businessId(businessId)
            .todayOrders(filterTodayOrders(myOrders))
            .totalOrders(myOrders.size())
            .totalWorkers(myWorkers.size())
            .totalRevenue(calculateRevenue(myOrders))
            .build();
    
    return ResponseEntity.ok(dashboard);
}
```

---

## 🔒 SECURITY VALIDATION

### **Automatic Access Control:**

```java
// Example: Ensure order belongs to current user's business
public Order updateOrder(Long orderId, UpdateOrderRequest request) {
    Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    
    // This will throw exception if:
    // - User is not ADMIN
    // - order.businessId != user's businessId
    businessContext.validateBusinessAccess(order.getBusinessId());
    
    // Safe to update
    order.setStatus(request.getStatus());
    return orderRepository.save(order);
}
```

---

## ✅ BENEFITS

### **1. Automatic Data Isolation**
- No need to manually add `WHERE business_id = ?` everywhere
- Centralized business logic

### **2. Role-Based Access**
- ADMIN sees everything
- OWNER sees their data only
- WORKER sees owner's data

### **3. Security**
- Prevents cross-business data access
- Validates every request

### **4. Simple Integration**
- Uses existing `CommonUtils.getLoggedInUser()`
- Integrates with existing User-Worker relationship

---

## 🎯 IMPLEMENTATION CHECKLIST

### **For Each Service:**

- [ ] Inject `BusinessContextFilter`
- [ ] Call `getCurrentBusinessId()` in list/query methods
- [ ] Add business ID to new entity creation
- [ ] Call `validateBusinessAccess()` in get/update methods

### **For Each Repository:**

- [ ] Add `findByBusinessId()` method
- [ ] Add `countByBusinessId()` method
- [ ] Add business_id to custom queries

### **For Each Controller:**

- [ ] Endpoints automatically filtered by business
- [ ] No changes needed (service handles it)

---

## 💡 EXAMPLES

### **Menu Service:**

```java
public List<MenuItem> getMenu() {
    Long businessId = businessContext.getCurrentBusinessId();
    return businessId == null 
        ? menuItemRepository.findAll() 
        : menuItemRepository.findByBusinessId(businessId);
}
```

### **Table Service:**

```java
public List<RestaurantTable> getTables() {
    Long businessId = businessContext.getCurrentBusinessId();
    return businessId == null
        ? tableRepository.findAll()
        : tableRepository.findByBusinessId(businessId);
}
```

### **Reservation Service:**

```java
public TableReservation createReservation(CreateReservationRequest request) {
    Long businessId = businessContext.getCurrentBusinessId();
    
    TableReservation reservation = TableReservation.builder()
            .businessId(businessId) // Auto-assign
            .customerName(request.getCustomerName())
            // ... other fields
            .build();
    
    return reservationRepository.save(reservation);
}
```

---

## 🚨 IMPORTANT NOTES

1. **Always call `getCurrentBusinessId()` for list queries**
2. **Always set `businessId` when creating entities**
3. **Always call `validateBusinessAccess()` for single-entity operations**
4. **ADMIN users get `null` business ID (sees all)**
5. **WORKER users get their owner's business ID**

---

## 🎊 SUMMARY

The `BusinessContextFilter` provides:
- ✅ Automatic business isolation
- ✅ Role-based access control
- ✅ Simple, centralized logic
- ✅ Security validation
- ✅ Integration with existing code

**Next Step:** Update your services to use `businessContext.getCurrentBusinessId()`!

---

**Status:** ✅ **Ready to Use**  
**Integration:** Inject `@Autowired BusinessContextFilter businessContext` in services
