# 🧪 SYSTEM TESTS - Multi-Tenant QR Ordering

**Date:** October 1, 2025, 7:29 PM  
**Status:** Application Running - Testing Now!

---

## ✅ BASIC TESTS (No Authentication Required)

### **TEST 1: Health Check**

**Purpose:** Verify guest API is working

**Command:**
```bash
curl http://localhost:9092/api/public/guest/health
```

**Expected Response:**
```json
{
  "message": "Guest ordering API is running",
  "status": "OK"
}
```

**Status:** ⏳ Run this test

---

### **TEST 2: Application Port**

**Purpose:** Verify app is listening on port 9092

**Command:**
```bash
curl http://localhost:9092/actuator/health
```

**OR:**
```bash
netstat -an | findstr "9092"
```

**Expected:** Connection successful

**Status:** ⏳ Run this test

---

## 🔐 AUTHENTICATED TESTS (Requires Login)

### **TEST 3: Generate QR Code (Owner)**

**Purpose:** Test QR code generation for a table

**Prerequisites:**
- Must be logged in as OWNER
- Must have a table in database (table_id = 1)

**Command:**
```bash
curl -X POST http://localhost:9092/api/hotel/qr-codes/generate/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

**Expected Response:**
```json
{
  "id": 1,
  "businessId": YOUR_BUSINESS_ID,
  "tableId": 1,
  "tableNumber": "T1",
  "qrToken": "abc123xyz...",
  "qrCodeUrl": "https://yourhotel.com/menu?token=abc123xyz",
  "qrCodeImage": "data:image/png;base64,...",
  "isActive": true,
  "scanCount": 0
}
```

**Status:** ⏳ Need auth token

---

### **TEST 4: List All QR Codes (Owner)**

**Purpose:** Get all QR codes for your business

**Command:**
```bash
curl http://localhost:9092/api/hotel/qr-codes \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Expected Response:**
```json
{
  "qrCodes": [...],
  "count": X
}
```

**Status:** ⏳ Need auth token

---

## 🎯 GUEST ORDERING TESTS (No Auth Required)

### **TEST 5: Scan QR Code**

**Purpose:** Simulate customer scanning QR code

**Prerequisites:**
- Must have generated a QR code (Test 3)
- Need the `qrToken` from Test 3 response

**Command:**
```bash
curl "http://localhost:9092/api/public/guest/scan?token=YOUR_QR_TOKEN"
```

**Expected Response:**
```json
{
  "id": 1,
  "sessionToken": "guest-xyz789...",
  "businessId": YOUR_BUSINESS_ID,
  "tableId": 1,
  "tableNumber": "T1",
  "status": "ACTIVE",
  "expiresAt": "2025-10-01T22:29:00"
}
```

**Important:** Save the `sessionToken` for next tests!

**Status:** ⏳ Need QR token

---

### **TEST 6: View Menu as Guest**

**Purpose:** Get menu items (filtered by business)

**Prerequisites:**
- Must have guest session (Test 5)
- Need `sessionToken` from Test 5

**Command:**
```bash
curl http://localhost:9092/api/public/guest/menu \
  -H "X-Guest-Session: YOUR_SESSION_TOKEN"
```

**Expected Response:**
```json
{
  "items": [
    {
      "id": 1,
      "name": "Pizza",
      "price": 12.99,
      "categoryName": "Main Course",
      "isAvailable": true
    }
  ],
  "count": X
}
```

**Status:** ⏳ Need session token

---

### **TEST 7: Place Guest Order**

**Purpose:** Place an order as a guest

**Prerequisites:**
- Guest session (Test 5)
- Menu item ID from Test 6

**Command:**
```bash
curl -X POST http://localhost:9092/api/public/guest/orders \
  -H "X-Guest-Session: YOUR_SESSION_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "guestName": "Test Customer",
    "guestPhone": "+1234567890",
    "items": [
      {
        "menuItemId": 1,
        "quantity": 2
      }
    ],
    "specialInstructions": "Extra sauce"
  }'
```

**Expected Response:**
```json
{
  "id": 123,
  "businessId": YOUR_BUSINESS_ID,
  "orderNumber": "ORD-20251001192900",
  "customerName": "Test Customer",
  "tableNumber": "T1",
  "status": "PENDING",
  "totalAmount": 27.29
}
```

**Status:** ⏳ Need session token

---

### **TEST 8: Track Order Status**

**Purpose:** Check order status in real-time

**Prerequisites:**
- Order ID from Test 7
- Session token

**Command:**
```bash
curl http://localhost:9092/api/public/guest/orders/123 \
  -H "X-Guest-Session: YOUR_SESSION_TOKEN"
```

**Expected Response:**
```json
{
  "id": 123,
  "orderNumber": "ORD-...",
  "status": "PENDING",
  "estimatedDeliveryTime": "2025-10-01T20:00:00"
}
```

**Status:** ⏳ Need order ID

---

## 🛡️ SECURITY TESTS

### **TEST 9: Cross-Business Access**

**Purpose:** Verify business isolation

**Test A: Try to access another business's QR code**
```bash
# Login as Business 1
# Try to get Business 2's QR code
curl http://localhost:9092/api/hotel/qr-codes/BUSINESS2_QR_ID \
  -H "Authorization: Bearer BUSINESS1_TOKEN"
```

**Expected:** Access denied or not found

**Test B: Try to use another business's session token**
```bash
# Get Business 1's session
# Try to access Business 2's menu
```

**Expected:** Access denied

**Status:** ⏳ Need multiple businesses

---

## 📊 TEST RESULTS TRACKER

| # | Test Name | Status | Notes |
|---|-----------|--------|-------|
| 1 | Health Check | ⏳ | Public endpoint |
| 2 | Port Check | ⏳ | Port 9092 |
| 3 | Generate QR | ⏳ | Need auth |
| 4 | List QR Codes | ⏳ | Need auth |
| 5 | Scan QR | ⏳ | Need QR token |
| 6 | View Menu | ⏳ | Need session |
| 7 | Place Order | ⏳ | Need session |
| 8 | Track Order | ⏳ | Need order ID |
| 9 | Security | ⏳ | Need 2 businesses |

**Legend:** ✅ Pass | ❌ Fail | ⏳ Pending

---

## 🔑 GETTING AUTH TOKEN

**To get JWT token for authenticated tests:**

1. **Login as owner:**
```bash
curl -X POST http://localhost:9092/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "owner@example.com",
    "password": "your_password"
  }'
```

2. **Copy the token from response:**
```json
{
  "accessToken": "eyJhbGc...",
  "tokenType": "Bearer"
}
```

3. **Use in subsequent requests:**
```bash
-H "Authorization: Bearer eyJhbGc..."
```

---

## 🎯 QUICK TEST SEQUENCE

**For a quick validation:**

1. ✅ Run Test 1 (Health Check) - Should work
2. 🔐 Login to get auth token
3. 🏗️ Create a table (if not exists)
4. 🎫 Run Test 3 (Generate QR) - Get QR token
5. 📱 Run Test 5 (Scan QR) - Get session token
6. 🍕 Run Test 6 (View Menu) - Should see menu
7. 🛒 Run Test 7 (Place Order) - Should create order
8. 📊 Run Test 8 (Track Order) - Should see status

---

## ✅ SUCCESS CRITERIA

**System is working correctly if:**

- ✅ Health check returns OK
- ✅ Can generate QR codes with business_id
- ✅ Can scan QR codes without auth
- ✅ Guest session has correct business_id
- ✅ Menu is filtered by business_id
- ✅ Orders are assigned correct business_id
- ✅ Can track order status
- ✅ Cross-business access is blocked

---

## 🐛 TROUBLESHOOTING

**If Test 1 fails:**
- Check if app is running: `netstat -an | findstr 9092`
- Check application logs
- Verify port 9092 is not blocked

**If Test 3 fails:**
- Verify JWT token is valid
- Check if table exists in database
- Verify user has OWNER role
- Check user has business_id set

**If Test 5 fails:**
- Verify QR token from Test 3
- Check qr_codes table has data
- Verify QR code is active

**If Test 6 fails:**
- Verify session token from Test 5
- Check if menu items exist with business_id
- Verify session hasn't expired

**If Test 7 fails:**
- Verify menu item IDs are correct
- Check session is valid
- Verify table exists

---

## 📝 NEXT STEPS

After tests pass:
1. ✅ Generate QR codes for all tables
2. ✅ Download QR images
3. ✅ Print and place on tables
4. ✅ Test with real devices
5. ✅ Go live!

---

**Start with Test 1! Copy the curl command and run it in your terminal.** 🚀
