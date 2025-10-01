# ⚡ QUICK TEST START GUIDE

**Updated:** October 1, 2025, 6:50 PM  
**Status:** Foreign key issues fixed - ready to test!

---

## 🎯 3-STEP QUICK START

### **STEP 1: Database Setup (2 mins)**

Run these SQL commands in your MySQL database:

```sql
-- Create QR Codes table
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
    INDEX idx_qr_token (qr_token)
);

-- Create Guest Sessions table
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
```

---

### **STEP 2: Start Application (1 min)**

```bash
./gradlew bootRun
```

Wait for:
```
Started Application in X.XXX seconds
```

---

### **STEP 3: Test Basic Functionality (5 mins)**

#### **Test 1: Check Health**
```bash
curl http://localhost:8080/api/public/guest/health
```

**Expected:**
```json
{
  "message": "Guest ordering API is running",
  "status": "OK"
}
```

#### **Test 2: Generate QR Code (Owner)**
```bash
POST http://localhost:8080/api/hotel/qr-codes/generate/1
Authorization: Bearer YOUR_OWNER_TOKEN
Content-Type: application/json
```

**Expected:**
```json
{
  "id": 1,
  "businessId": 1,
  "tableId": 1,
  "qrToken": "abc123xyz...",
  "qrCodeUrl": "https://yourhotel.com/menu?token=abc123xyz",
  "isActive": true
}
```

**Save the `qrToken` for next step!**

#### **Test 3: Scan QR Code (Guest - No Auth)**
```bash
GET http://localhost:8080/api/public/guest/scan?token=YOUR_QR_TOKEN
```

**Expected:**
```json
{
  "sessionToken": "guest-xyz789...",
  "businessId": 1,
  "tableId": 1,
  "tableNumber": "T1",
  "status": "ACTIVE"
}
```

**✅ If all 3 tests pass → System is working!**

---

## 📋 PREREQUISITES CHECKLIST

Before testing, ensure you have:

- [ ] MySQL running
- [ ] Database created
- [ ] Tables created (qr_codes, guest_sessions)
- [ ] At least one table in hotel_restaurant_tables
- [ ] At least one menu item
- [ ] User with OWNER role and business_id set

---

## 🔧 WHAT WAS FIXED

**Issue:** Foreign key constraint violations on `table_id`

**Fix:** 
- Removed `@ManyToOne` relationships from QRCode and GuestSession
- Tables now use simple `table_id` columns (no FK constraints)
- Table details fetched separately when needed

**Impact:**
- ✅ No more FK errors
- ✅ More flexible data management
- ✅ Business-level validation still enforced
- ✅ Performance optimized

---

## 🎯 FULL TESTING SEQUENCE

If quick tests pass, run the full test suite:

### **1. Multi-Tenant Orders**
```bash
POST /api/hotel/orders
# Verify order has correct business_id
```

### **2. QR Code Management**
```bash
POST /api/hotel/qr-codes/generate/{tableId}
GET /api/hotel/qr-codes
GET /api/hotel/qr-codes/{id}/download
```

### **3. Guest Ordering Flow**
```bash
# Scan QR
GET /api/public/guest/scan?token=xyz

# View Menu
GET /api/public/guest/menu
Header: X-Guest-Session: session-token

# Place Order
POST /api/public/guest/orders
Header: X-Guest-Session: session-token

# Track Order
GET /api/public/guest/orders/{id}
Header: X-Guest-Session: session-token
```

### **4. Business Isolation**
```bash
# Try to access other business's data
# Should be denied!
```

---

## 🚨 TROUBLESHOOTING

### **Application Won't Start**
```bash
# Check database connection
# Verify application.properties has correct DB settings
spring.datasource.url=jdbc:mysql://localhost:3306/user-master
spring.datasource.username=root
spring.datasource.password=your_password
```

### **Table Not Found Error**
```sql
-- Run database migration SQL
-- See COMPLETE_SYSTEM_TEST.md for full SQL
```

### **Cannot Generate QR Code**
```bash
# Ensure ZXing dependency is present
# Run: ./gradlew clean build
# Check build.gradle has:
implementation 'com.google.zxing:core:3.5.3'
implementation 'com.google.zxing:javase:3.5.3'
```

---

## ✅ SUCCESS INDICATORS

**System is working if:**

1. ✅ Application starts without errors
2. ✅ Can generate QR code for table
3. ✅ QR code has correct business_id
4. ✅ Can scan QR code (no auth)
5. ✅ Guest session has correct business_id
6. ✅ Can view menu as guest
7. ✅ Can place order as guest
8. ✅ Order has correct business_id
9. ✅ Cross-business access blocked

---

## 📚 DOCUMENTATION

**Complete Guides:**
- `COMPLETE_SYSTEM_TEST.md` - Full testing guide
- `FIXES_APPLIED.md` - Details of fixes made
- `QR_CODE_COMPLETE.md` - Complete QR code implementation
- `TESTING_NOW.md` - General testing guide

---

## 🎊 NEXT AFTER TESTING

Once all tests pass:

1. **Print QR Codes**
   - Download QR images via API
   - Print on stickers/cards
   - Place on restaurant tables

2. **Train Staff**
   - Show how to generate QR codes
   - Explain guest ordering process
   - Monitor initial orders

3. **Go Live!**
   - Enable for customers
   - Monitor system
   - Collect feedback

---

**🚀 Ready to test? Run Step 1, 2, 3 above!**

**Need help? Check the troubleshooting section or refer to full documentation.**
