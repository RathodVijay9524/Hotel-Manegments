# 🚀 QUICK START - NEXT STEPS

**Current Status:** Application compiles ✅  
**Next:** Database Migration + Service Updates

---

## ⚡ STEP-BY-STEP GUIDE

### **STEP 1: Run Application (Verify It Starts) - 2 minutes**

```bash
./gradlew bootRun
```

**Expected:** Application starts without errors

**If it starts successfully, STOP it (Ctrl+C) and proceed to Step 2**

---

### **STEP 2: Database Migration - 5 minutes**

⚠️ **IMPORTANT:** Backup your database first!

**Option A: Using MySQL Command Line**
```bash
mysql -u your_username -p your_database_name < database_migration.sql
```

**Option B: Using phpMyAdmin / MySQL Workbench**
1. Open `database_migration.sql` file
2. Copy all SQL statements
3. Paste into SQL editor
4. Execute

**Verify Migration:**
```sql
-- Check if business_id column exists
DESCRIBE hotel_orders;

-- Check if indexes exist
SHOW INDEX FROM hotel_orders WHERE Key_name LIKE 'idx%';

-- Check user business fields
DESCRIBE users;
```

**Expected Results:**
- `business_id` column exists in all hotel tables
- Indexes created (idx_orders_business_id, etc.)
- User table has business_name, business_address, etc.

---

### **STEP 3: Start Application Again - 2 minutes**

```bash
./gradlew bootRun
```

**Expected:** 
- Application starts successfully
- No errors about missing columns
- Application runs normally

---

### **STEP 4: Test Business Context - 5 minutes**

**Test Endpoint Created:** `TestMultiTenantController`

#### **Test 1: Check Business Context**

Login to your application, then:

```bash
# Using curl (replace YOUR_TOKEN with actual JWT token)
curl http://localhost:8080/api/test/multi-tenant/context \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**OR using Postman/Browser:**
```
GET http://localhost:8080/api/test/multi-tenant/context
Authorization: Bearer YOUR_TOKEN
```

**Expected Response:**
```json
{
  "businessId": 1,
  "isAdmin": false,
  "isOwner": true,
  "userId": 1,
  "username": "your_username",
  "message": "✅ You are OWNER - You see YOUR business (ID: 1)",
  "status": "SUCCESS"
}
```

#### **Test 2: Health Check**

```bash
curl http://localhost:8080/api/test/multi-tenant/health
```

**Expected:**
```json
{
  "multiTenantEnabled": true,
  "businessContextFilterActive": true,
  "message": "✅ Multi-tenant system is ready!"
}
```

---

### **STEP 5: Update Services - 1 hour**

Now that the foundation works, update your services to use business filtering.

#### **Priority Services to Update:**

1. **OrderService** (15 mins)
2. **MenuService** (15 mins)
3. **CategoryService** (10 mins)
4. **RestaurantTableService** (10 mins)
5. **ReservationService** (10 mins)

#### **Pattern for EACH Service:**

```java
@Service
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private BusinessContextFilter businessContext; // ADD THIS
    
    // UPDATE LIST METHODS
    public List<Order> getAllOrders() {
        Long businessId = businessContext.getCurrentBusinessId();
        
        return businessId == null 
            ? orderRepository.findAll() 
            : orderRepository.findByBusinessId(businessId);
    }
    
    // UPDATE CREATE METHODS
    public Order createOrder(CreateOrderRequest request) {
        Long businessId = businessContext.getCurrentBusinessId();
        
        Order order = Order.builder()
                .businessId(businessId) // ADD THIS LINE
                .orderNumber(generateOrderNumber())
                // ... rest of fields
                .build();
        
        return orderRepository.save(order);
    }
    
    // UPDATE GET BY ID METHODS
    public Order getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        
        // ADD THIS LINE
        businessContext.validateBusinessAccess(order.getBusinessId());
        
        return order;
    }
}
```

---

### **STEP 6: Test Each Service - 5 minutes per service**

After updating each service, test it:

**Test Order Creation:**
```bash
curl -X POST http://localhost:8080/api/hotel/orders \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "Test Customer",
    "orderType": "DINE_IN"
  }'
```

**Expected:**
- Order created successfully
- Response includes `"businessId": 1`

**Test Order Listing:**
```bash
curl http://localhost:8080/api/hotel/orders \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Expected:**
- Only orders for your business ID shown
- No orders from other businesses

---

## 📋 CHECKLIST

- [ ] Step 1: Application starts without errors
- [ ] Step 2: Database migration completed
- [ ] Step 3: Application starts again (with new columns)
- [ ] Step 4: Test endpoint shows correct business context
- [ ] Step 5: OrderService updated
- [ ] Step 6: MenuService updated
- [ ] Step 7: CategoryService updated
- [ ] Step 8: Other services updated (optional)
- [ ] Step 9: Full system test

---

## 🎯 WHAT EACH STEP ACHIEVES

**Step 1:** ✅ Confirms code compiles  
**Step 2:** ✅ Database has business_id columns  
**Step 3:** ✅ Application works with new schema  
**Step 4:** ✅ Business context filter working  
**Step 5:** ✅ Services use business filtering  
**Step 6:** ✅ End-to-end testing complete

---

## 🚨 TROUBLESHOOTING

### **Issue: "Column 'business_id' not found"**
**Solution:** Run database migration (Step 2)

### **Issue: "business_id cannot be null"**
**Solution:** User needs OWNER role:
```sql
INSERT INTO users_roles (user_id, role_id) 
VALUES (1, (SELECT id FROM roles WHERE name = 'ROLE_OWNER'));
```

### **Issue: Test endpoint returns 404**
**Solution:** Application needs restart after adding controller

### **Issue: "Access denied"**
**Solution:** This is correct! Shows data isolation working

---

## 💡 TIPS

1. **Test as you go** - Don't update all services at once
2. **Keep a backup** - Database backup before migration
3. **Use test endpoint** - Verify business context first
4. **Check logs** - Watch for any errors
5. **One service at a time** - Update and test incrementally

---

## 🎊 AFTER COMPLETION

Once all steps are done, you'll have:

✅ **Complete Multi-Tenant System**
- Each owner sees only their data
- Workers see their owner's data  
- Admin sees everything
- Automatic security enforcement

✅ **Ready for QR Code Ordering**
- Solid foundation
- Business context ready
- Easy to add guest ordering

---

## 📚 REFERENCE DOCUMENTS

- **READY_TO_DEPLOY.md** - Overview and deployment guide
- **IMPLEMENTATION_COMPLETE_GUIDE.md** - Detailed patterns
- **BUSINESS_CONTEXT_USAGE_GUIDE.md** - Code examples
- **database_migration.sql** - SQL to run

---

## ⏱️ TIME ESTIMATE

- Step 1: 2 mins
- Step 2: 5 mins (database migration)
- Step 3: 2 mins
- Step 4: 5 mins (testing)
- Step 5: 1 hour (service updates)
- Step 6: 30 mins (testing)

**Total: ~1.5 hours**

---

## 🚀 READY?

**Start with Step 1!** Run the application and make sure it compiles.

Then proceed step-by-step through the checklist.

**Need help?** Check the reference documents or ask me!

---

**Status:** 📋 **CHECKLIST READY**  
**Next:** Run `./gradlew bootRun` and verify it starts
