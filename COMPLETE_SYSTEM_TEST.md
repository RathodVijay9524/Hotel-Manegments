# 🧪 COMPLETE SYSTEM TEST GUIDE

**Date:** October 1, 2025, 6:40 PM  
**Goal:** Test Multi-Tenant + QR Code Ordering System

---

## 📋 PRE-TEST CHECKLIST

### **Step 1: Verify Build Dependencies**
✅ Check if all dependencies are in build.gradle:
- Spring Boot
- MySQL
- JWT
- ZXing (for QR codes)

### **Step 2: Database Setup**
Run these SQL scripts in order:

```sql
-- 1. Add business_id columns to existing tables (if not done)
ALTER TABLE hotel_orders ADD COLUMN business_id BIGINT AFTER id;
ALTER TABLE hotel_menu_items ADD COLUMN business_id BIGINT AFTER id;
ALTER TABLE hotel_categories ADD COLUMN business_id BIGINT AFTER id;
ALTER TABLE hotel_tables ADD COLUMN business_id BIGINT AFTER id;
ALTER TABLE hotel_reservations ADD COLUMN business_id BIGINT AFTER id;

-- 2. Create QR codes table
CREATE TABLE IF NOT EXISTS qr_codes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    business_id BIGINT NOT NULL,
    table_id BIGINT NOT NULL,
    qr_token VARCHAR(100) UNIQUE NOT NULL,
    qr_code_url VARCHAR(500) NOT NULL,
    qr_code_image LONGTEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    scan_count INT DEFAULT 0,
    last_scanned_at DATETIME,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    INDEX idx_qr_business_id (business_id),
    INDEX idx_qr_token (qr_token),
    UNIQUE KEY uk_business_table (business_id, table_id)
);

-- 3. Create guest sessions table
CREATE TABLE IF NOT EXISTS guest_sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    business_id BIGINT NOT NULL,
    table_id BIGINT NOT NULL,
    session_token VARCHAR(100) UNIQUE NOT NULL,
    guest_name VARCHAR(100),
    guest_phone VARCHAR(20),
    guest_email VARCHAR(100),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    device_info VARCHAR(500),
    created_at DATETIME NOT NULL,
    expires_at DATETIME NOT NULL,
    completed_at DATETIME,
    INDEX idx_guest_business_id (business_id),
    INDEX idx_guest_token (session_token),
    INDEX idx_guest_table (table_id)
);

-- 4. Add business fields to users (if not done)
ALTER TABLE users ADD COLUMN business_id BIGINT AFTER id;
ALTER TABLE users ADD COLUMN business_name VARCHAR(255) AFTER business_id;
ALTER TABLE users ADD COLUMN business_type VARCHAR(50) AFTER business_name;

-- 5. Create indexes
CREATE INDEX idx_orders_business ON hotel_orders(business_id);
CREATE INDEX idx_menu_business ON hotel_menu_items(business_id);
CREATE INDEX idx_categories_business ON hotel_categories(business_id);
CREATE INDEX idx_tables_business ON hotel_tables(business_id);
CREATE INDEX idx_reservations_business ON hotel_reservations(business_id);
```

---

## 🚀 TESTING SEQUENCE

### **TEST 1: Compile & Start Application (5 mins)**

**Step 1.1: Clean and Build**
```bash
./gradlew clean build
```

**Expected:** Build SUCCESS

**Step 1.2: Start Application**
```bash
./gradlew bootRun
```

**Expected:** 
- Application starts on port 8080
- No errors in console
- "Started Application in X seconds"

**✅ PASS if:** Application starts without errors  
**❌ FAIL if:** Compilation errors or startup fails

---

### **TEST 2: Business Context Test (2 mins)**

**Step 2.1: Test Business Context Endpoint**

```bash
# Using Postman or curl
GET http://localhost:8080/api/test/multi-tenant/context
Authorization: Bearer YOUR_JWT_TOKEN
```

**Expected Response (Owner):**
```json
{
  "businessId": 1,
  "businessIdStatus": "SPECIFIC_BUSINESS",
  "isOwner": true,
  "userId": 1,
  "message": "✅ You are OWNER - You see YOUR business (ID: 1)"
}
```

**Expected Response (Admin):**
```json
{
  "businessId": null,
  "businessIdStatus": "ALL_BUSINESSES (Admin)",
  "isAdmin": true,
  "message": "✅ You are ADMIN - You can see ALL businesses"
}
```

**✅ PASS if:** You get correct business_id based on your role  
**❌ FAIL if:** businessId is null for owner or wrong value

---

### **TEST 3: Multi-Tenant Order Test (5 mins)**

**Step 3.1: Create Test Data**

First, ensure you have:
- A user with OWNER role (business_id = 1)
- A menu item (id = 1)
- A table (id = 1)

**Step 3.2: Create Order (Owner 1)**

```bash
POST http://localhost:8080/api/hotel/orders
Authorization: Bearer OWNER1_TOKEN
Content-Type: application/json

{
  "userId": 1,
  "customerName": "Test Customer",
  "customerPhone": "+1234567890",
  "orderType": "DINE_IN",
  "tableId": 1,
  "items": [
    {
      "menuItemId": 1,
      "quantity": 2
    }
  ]
}
```

**Expected Response:**
```json
{
  "id": 1,
  "businessId": 1,  // ← MUST match your business_id!
  "orderNumber": "ORD-...",
  "customerName": "Test Customer",
  "status": "PENDING",
  "totalAmount": 25.98
}
```

**Step 3.3: Verify Business Isolation**

```bash
# List orders
GET http://localhost:8080/api/hotel/orders
Authorization: Bearer OWNER1_TOKEN
```

**Expected:** Only orders with YOUR business_id

**✅ PASS if:** Order has correct business_id  
**❌ FAIL if:** business_id is null or wrong

---

### **TEST 4: QR Code Generation Test (5 mins)**

**Step 4.1: Generate QR Code**

```bash
POST http://localhost:8080/api/hotel/qr-codes/generate/1
Authorization: Bearer OWNER_TOKEN
```

**Expected Response:**
```json
{
  "id": 1,
  "businessId": 1,
  "tableId": 1,
  "tableNumber": "T1",
  "qrToken": "abc123...",
  "qrCodeUrl": "https://yourhotel.com/menu?token=abc123...",
  "qrCodeImage": "data:image/png;base64,iVBORw0KG...",
  "isActive": true,
  "scanCount": 0
}
```

**Step 4.2: Download QR Code Image**

```bash
GET http://localhost:8080/api/hotel/qr-codes/1/download
Authorization: Bearer OWNER_TOKEN
```

**Expected:** PNG image file downloads

**✅ PASS if:** QR code created with correct business_id  
**❌ FAIL if:** Error or business_id mismatch

---

### **TEST 5: Guest Ordering Test (10 mins)**

**Step 5.1: Scan QR Code (No Auth)**

```bash
GET http://localhost:8080/api/public/guest/scan?token=YOUR_QR_TOKEN
```

**Expected Response:**
```json
{
  "sessionToken": "guest-xyz789...",
  "businessId": 1,  // ← Inherited from QR code!
  "tableId": 1,
  "tableNumber": "T1",
  "status": "ACTIVE",
  "expiresAt": "2025-10-01T21:40:00"
}
```

**Save the sessionToken for next steps!**

**Step 5.2: View Menu as Guest**

```bash
GET http://localhost:8080/api/public/guest/menu
X-Guest-Session: guest-xyz789...
```

**Expected Response:**
```json
{
  "items": [
    {
      "id": 1,
      "name": "Pizza",
      "price": 12.99,
      "businessId": 1  // Only items from business_id=1
    }
  ],
  "count": 5
}
```

**Step 5.3: Place Guest Order**

```bash
POST http://localhost:8080/api/public/guest/orders
X-Guest-Session: guest-xyz789...
Content-Type: application/json

{
  "guestName": "John Guest",
  "guestPhone": "+1234567890",
  "items": [
    {
      "menuItemId": 1,
      "quantity": 1
    }
  ]
}
```

**Expected Response:**
```json
{
  "id": 2,
  "businessId": 1,  // ← Auto-assigned from session!
  "orderNumber": "ORD-...",
  "customerName": "John Guest",
  "tableNumber": "T1",
  "status": "PENDING",
  "totalAmount": 13.64
}
```

**Step 5.4: Track Order Status**

```bash
GET http://localhost:8080/api/public/guest/orders/2
X-Guest-Session: guest-xyz789...
```

**Expected:** Order details with current status

**✅ PASS if:** 
- Session created with correct business_id
- Menu filtered by business_id
- Order assigned correct business_id
- Can track order status

**❌ FAIL if:** Any step shows wrong business data

---

### **TEST 6: Security Test - Cross-Business Access (5 mins)**

**Step 6.1: Try to Access Different Business's Order**

Assuming you're Owner 1 (business_id=1), try to access an order from business_id=2:

```bash
# Create order as Owner 2 first (if you have access)
# Then try to access it as Owner 1

GET http://localhost:8080/api/hotel/orders/999
Authorization: Bearer OWNER1_TOKEN
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

**Step 6.2: Try to Use Different Business's QR Code**

Create QR for business_id=2, then scan with business_id=1 session:

```bash
GET http://localhost:8080/api/public/guest/orders/X
X-Guest-Session: business1-session
# Try to access business2's order
```

**Expected:** Access denied or not found

**✅ PASS if:** Cross-business access is blocked  
**❌ FAIL if:** Can access other business's data

---

## 📊 TEST RESULTS CHECKLIST

Fill this out as you test:

| Test # | Test Name | Status | Notes |
|--------|-----------|--------|-------|
| 1 | Application Starts | ⏳ | |
| 2 | Business Context | ⏳ | businessId: ___ |
| 3 | Multi-Tenant Orders | ⏳ | businessId: ___ |
| 4 | QR Code Generation | ⏳ | Token: ___ |
| 5 | Guest Scan QR | ⏳ | Session: ___ |
| 6 | Guest View Menu | ⏳ | Items: ___ |
| 7 | Guest Place Order | ⏳ | Order #: ___ |
| 8 | Guest Track Order | ⏳ | Status: ___ |
| 9 | Security - Cross Business | ⏳ | Blocked: Y/N |

**Legend:** ✅ Pass | ❌ Fail | ⏳ Pending

---

## 🔧 TROUBLESHOOTING

### **Issue: "Column 'business_id' not found"**
**Solution:** Run database migration SQL above

### **Issue: "businessId is null in response"**
**Solution:** 
- Verify user has business_id set in database
- Check user has OWNER role
- Verify BusinessContextFilter is working

### **Issue: "Cannot generate QR code image"**
**Solution:** 
- Verify ZXing dependency is in build.gradle
- Run `./gradlew clean build` to download dependencies

### **Issue: "QR code scan returns 404"**
**Solution:** 
- Verify QR token is correct
- Check qr_codes table exists
- Ensure QR code is active

### **Issue: "Guest session expired"**
**Solution:** Sessions expire after 3 hours - scan QR again

---

## ✅ SUCCESS CRITERIA

**All tests pass if:**

1. ✅ Application compiles and starts
2. ✅ Business context shows correct business_id
3. ✅ Orders auto-assign business_id
4. ✅ Menu items filtered by business_id
5. ✅ QR codes generated with business_id
6. ✅ Guest sessions inherit business_id from QR
7. ✅ Guest orders assigned correct business_id
8. ✅ Cross-business access is blocked
9. ✅ Each owner sees only their data

---

## 🎯 FINAL VALIDATION

**The system works correctly if:**

### **For Karina (business_id=2):**
1. Generates QR code → Has business_id=2
2. Customer scans QR → Session has business_id=2
3. Customer views menu → Sees only Karina's items
4. Customer orders → Order has business_id=2
5. Karina sees order → In her dashboard
6. John (business_id=1) → Cannot see Karina's order ✅

### **For John (business_id=1):**
1. Generates QR code → Has business_id=1
2. Customer scans QR → Session has business_id=1
3. Customer views menu → Sees only John's items
4. Customer orders → Order has business_id=1
5. John sees order → In his dashboard
6. Karina (business_id=2) → Cannot see John's order ✅

**Perfect isolation achieved!** 🎊

---

## 📝 POST-TEST ACTIONS

If all tests pass:
1. ✅ Mark system as production-ready
2. ✅ Generate QR codes for all tables
3. ✅ Print and deploy QR codes
4. ✅ Train staff on system
5. ✅ Monitor initial orders

If tests fail:
1. ❌ Check error logs
2. ❌ Review database schema
3. ❌ Verify code changes
4. ❌ Re-run failed tests
5. ❌ Ask for help if needed

---

**Ready to start testing? Let's go! 🚀**
