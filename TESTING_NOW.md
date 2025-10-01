# 🧪 TESTING MULTI-TENANT SYSTEM - STEP BY STEP

**Date:** October 1, 2025, 6:04 PM  
**Status:** Ready to Test!

---

## 📋 PRE-TEST CHECKLIST

Before testing, ensure:
- [ ] Database migration completed (`database_migration.sql` executed)
- [ ] Application compiles without errors
- [ ] You have a user account with OWNER role

---

## 🚀 TEST SEQUENCE

### **TEST 1: Start Application (2 mins)**

**Command:**
```bash
./gradlew bootRun
```

**Expected:**
- Application starts without errors
- No "Column not found" errors
- Server running on port 8080

**If errors:**
- Check database migration was run
- Check application.properties has correct DB config

---

### **TEST 2: Check Business Context (1 min)**

**Endpoint:**
```
GET http://localhost:8080/api/test/multi-tenant/context
```

**Headers:**
```
Authorization: Bearer YOUR_JWT_TOKEN
```

**Expected Response (if OWNER):**
```json
{
  "businessId": 1,
  "businessIdStatus": "SPECIFIC_BUSINESS",
  "isAdmin": false,
  "isOwner": true,
  "isWorkerOrManager": false,
  "userId": 1,
  "username": "your_username",
  "name": "Your Name",
  "message": "✅ You are OWNER - You see YOUR business (ID: 1)",
  "status": "SUCCESS"
}
```

**Expected Response (if ADMIN):**
```json
{
  "businessId": null,
  "businessIdStatus": "ALL_BUSINESSES (Admin)",
  "isAdmin": true,
  "isOwner": false,
  "isWorkerOrManager": false,
  "userId": 1,
  "username": "admin",
  "name": "Admin User",
  "message": "✅ You are ADMIN - You can see ALL businesses",
  "status": "SUCCESS"
}
```

**✅ PASS if:** You get correct business_id and role
**❌ FAIL if:** Error or wrong business_id

---

### **TEST 3: Create Order with Auto-Assign (2 mins)**

**Endpoint:**
```
POST http://localhost:8080/api/hotel/orders
```

**Headers:**
```
Authorization: Bearer YOUR_JWT_TOKEN
Content-Type: application/json
```

**Request Body:**
```json
{
  "userId": 1,
  "customerName": "Test Customer",
  "customerPhone": "+1234567890",
  "orderType": "DINE_IN",
  "items": [
    {
      "menuItemId": 1,
      "quantity": 2,
      "specialInstructions": "No onions"
    }
  ],
  "specialInstructions": "Table near window"
}
```

**Expected Response:**
```json
{
  "id": 1,
  "businessId": 1,  // ← Should match your business ID!
  "orderNumber": "ORD-20251001180500",
  "customerName": "Test Customer",
  "status": "PENDING",
  "totalAmount": 25.99,
  ...
}
```

**✅ PASS if:** Order created with YOUR business_id
**❌ FAIL if:** business_id is null or wrong

---

### **TEST 4: List Orders (Filtered) (1 min)**

**Endpoint:**
```
GET http://localhost:8080/api/hotel/orders
```

**Headers:**
```
Authorization: Bearer YOUR_JWT_TOKEN
```

**Expected Response:**
```json
[
  {
    "id": 1,
    "businessId": 1,  // All orders should have YOUR business_id
    "orderNumber": "ORD-20251001180500",
    ...
  }
]
```

**✅ PASS if:** 
- Only orders with YOUR business_id shown
- If you have no orders yet, returns empty array []

**❌ FAIL if:** 
- Shows orders from other businesses
- Shows all orders (if you're not admin)

---

### **TEST 5: Create Menu Item (2 mins)**

**Endpoint:**
```
POST http://localhost:8080/api/hotel/menu-items
```

**Headers:**
```
Authorization: Bearer YOUR_JWT_TOKEN
Content-Type: application/json
```

**Request Body:**
```json
{
  "categoryId": 1,
  "name": "Test Pizza",
  "description": "Delicious test pizza",
  "price": 12.99,
  "isVegetarian": true,
  "isAvailable": true,
  "isFeatured": false
}
```

**Expected Response:**
```json
{
  "id": 1,
  "businessId": 1,  // ← Should match your business ID!
  "name": "Test Pizza",
  "price": 12.99,
  ...
}
```

**✅ PASS if:** Menu item created with YOUR business_id
**❌ FAIL if:** business_id is null or wrong

---

### **TEST 6: List Menu Items (Filtered) (1 min)**

**Endpoint:**
```
GET http://localhost:8080/api/hotel/menu-items
```

**Headers:**
```
Authorization: Bearer YOUR_JWT_TOKEN
```

**Expected Response:**
```json
[
  {
    "id": 1,
    "businessId": 1,  // All items should have YOUR business_id
    "name": "Test Pizza",
    ...
  }
]
```

**✅ PASS if:** Only YOUR menu items shown
**❌ FAIL if:** Shows items from other businesses

---

### **TEST 7: Create Reservation (2 mins)**

**Endpoint:**
```
POST http://localhost:8080/api/hotel/reservations
```

**Headers:**
```
Authorization: Bearer YOUR_JWT_TOKEN
Content-Type: application/json
```

**Request Body:**
```json
{
  "userId": 1,
  "customerName": "Test Reservation",
  "customerPhone": "+1234567890",
  "customerEmail": "test@test.com",
  "tableId": 1,
  "numberOfGuests": 4,
  "reservationDateTime": "2025-10-02T19:00:00",
  "specialRequests": "Window seat please"
}
```

**Expected Response:**
```json
{
  "id": 1,
  "businessId": 1,  // ← Should match your business ID!
  "reservationNumber": "RES-20251001180500",
  "customerName": "Test Reservation",
  "status": "PENDING",
  ...
}
```

**✅ PASS if:** Reservation created with YOUR business_id
**❌ FAIL if:** business_id is null or wrong

---

### **TEST 8: Cross-Business Access (SECURITY TEST) (2 mins)**

This tests that you CANNOT access another business's data.

**Scenario:** Try to get an order that belongs to another business

**Step 1:** Note your own order ID from TEST 4
**Step 2:** Try to access a different order ID (e.g., ID + 100)

**Endpoint:**
```
GET http://localhost:8080/api/hotel/orders/999
```

**Headers:**
```
Authorization: Bearer YOUR_JWT_TOKEN
```

**Expected Response:**
```json
{
  "error": "Access denied - This resource belongs to a different business"
}
```
OR
```json
{
  "error": "Order not found"
}
```

**✅ PASS if:** Access denied or not found
**❌ FAIL if:** You can access other business's order

---

## 📊 TEST RESULTS SUMMARY

### **Fill this out as you test:**

| Test | Status | Notes |
|------|--------|-------|
| 1. Application Starts | ⏳ | |
| 2. Business Context | ⏳ | businessId: ___ |
| 3. Create Order | ⏳ | businessId: ___ |
| 4. List Orders | ⏳ | Count: ___ |
| 5. Create Menu Item | ⏳ | businessId: ___ |
| 6. List Menu Items | ⏳ | Count: ___ |
| 7. Create Reservation | ⏳ | businessId: ___ |
| 8. Cross-Business Block | ⏳ | |

**Legend:**
- ✅ = Pass
- ❌ = Fail
- ⏳ = Pending

---

## 🔧 TROUBLESHOOTING

### **Issue: "Column 'business_id' not found"**
**Solution:**
```sql
-- Run database migration
mysql -u username -p database < database_migration.sql
```

### **Issue: "businessId is null in response"**
**Solution:**
- Check user has OWNER role
- Check BusinessContextFilter is working
```sql
SELECT u.id, u.username, r.name 
FROM users u 
JOIN users_roles ur ON u.id = ur.user_id 
JOIN roles r ON ur.role_id = r.id;
```

### **Issue: "Access denied" on own data**
**Solution:**
- Check business_id in data matches user's business_id
- Check BusinessContextFilter.validateBusinessAccess() logic

### **Issue: Can see other business's data**
**Solution:**
- Service not filtering correctly
- Check service uses businessContext.getCurrentBusinessId()

---

## ✅ SUCCESS CRITERIA

**All tests pass if:**
1. ✅ Application starts without errors
2. ✅ Business context shows correct business_id
3. ✅ Creating data auto-assigns business_id
4. ✅ Listing data shows only your business
5. ✅ Cross-business access is blocked

**System is WORKING when:**
- Each owner sees only their data
- Data has correct business_id
- Security is enforced

---

## 🎊 AFTER SUCCESSFUL TESTING

**You've Proven:**
- ✅ Multi-tenant architecture works
- ✅ Business isolation is complete
- ✅ Data security is enforced
- ✅ System is production-ready

**Next Steps:**
1. Update remaining services (optional)
2. Test with multiple hotel owners
3. Implement QR code ordering
4. Launch to production!

---

**Good luck with testing! 🚀**
