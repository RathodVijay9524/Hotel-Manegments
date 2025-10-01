# 🎯 TEST NOW - Application is Running!

**Time:** 7:30 PM, October 1, 2025  
**Status:** ✅ Application Running on Port 9092

---

## ⚡ QUICK TESTS (Copy & Paste)

### **TEST 1: Health Check (No Auth)** ✅

**Windows PowerShell:**
```powershell
Invoke-RestMethod -Uri "http://localhost:9092/api/public/guest/health"
```

**Command Prompt / Git Bash:**
```bash
curl http://localhost:9092/api/public/guest/health
```

**Expected:**
```
message: Guest ordering API is running
status: OK
```

---

### **TEST 2: View Swagger UI** 🌐

Open in browser:
```
http://localhost:9092/swagger-ui/index.html
```

**What you'll see:**
- All API endpoints documented
- Can test endpoints directly
- See request/response examples

---

### **TEST 3: Check Application Status** ✅

**PowerShell:**
```powershell
Test-NetConnection -ComputerName localhost -Port 9092
```

**Expected:** `TcpTestSucceeded : True`

---

## 🔐 AUTHENTICATED TESTS

### **Step 1: Get Auth Token**

**Do you have an owner account?**

**If YES:**
```powershell
$body = @{
    usernameOrEmail = "your_email@example.com"
    password = "your_password"
} | ConvertTo-Json

$login = Invoke-RestMethod -Uri "http://localhost:9092/api/auth/login" -Method Post -Body $body -ContentType "application/json"

# Save token
$token = $login.accessToken
Write-Host "Token: $token"
```

**If NO:** Create an owner account first

---

### **Step 2: Generate QR Code**

**Prerequisites:**
- Must have a table in database
- Must be logged in as OWNER
- Must have business_id set

**PowerShell:**
```powershell
$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

$qr = Invoke-RestMethod -Uri "http://localhost:9092/api/hotel/qr-codes/generate/1" -Method Post -Headers $headers

# Save QR token
$qrToken = $qr.qrToken
Write-Host "QR Token: $qrToken"
Write-Host "QR URL: $($qr.qrCodeUrl)"
```

**Expected Response:**
```json
{
  "id": 1,
  "businessId": YOUR_BUSINESS_ID,
  "tableId": 1,
  "qrToken": "abc123...",
  "isActive": true
}
```

---

### **Step 3: Test Guest Ordering**

**3A. Scan QR Code (No Auth):**
```powershell
$scan = Invoke-RestMethod -Uri "http://localhost:9092/api/public/guest/scan?token=$qrToken"

# Save session token
$sessionToken = $scan.sessionToken
Write-Host "Session Token: $sessionToken"
Write-Host "Business ID: $($scan.businessId)"
Write-Host "Table ID: $($scan.tableId)"
```

**Expected:** Guest session created with business_id

---

**3B. View Menu (No Auth):**
```powershell
$headers = @{
    "X-Guest-Session" = $sessionToken
}

$menu = Invoke-RestMethod -Uri "http://localhost:9092/api/public/guest/menu" -Headers $headers

Write-Host "Menu Items:"
$menu.items | ForEach-Object {
    Write-Host "  - $($_.name): $($_.price)"
}
```

**Expected:** Menu items filtered by business_id

---

**3C. Place Order (No Auth):**
```powershell
$orderRequest = @{
    guestName = "Test Customer"
    guestPhone = "+1234567890"
    items = @(
        @{
            menuItemId = 1
            quantity = 2
        }
    )
    specialInstructions = "Extra sauce"
} | ConvertTo-Json

$headers = @{
    "X-Guest-Session" = $sessionToken
    "Content-Type" = "application/json"
}

$order = Invoke-RestMethod -Uri "http://localhost:9092/api/public/guest/orders" -Method Post -Body $orderRequest -Headers $headers

Write-Host "Order Created:"
Write-Host "  Order #: $($order.orderNumber)"
Write-Host "  Business ID: $($order.businessId)"
Write-Host "  Total: $($order.totalAmount)"
```

**Expected:** Order created with business_id from session

---

## ✅ SUCCESS INDICATORS

**System is working if:**

1. ✅ Health check returns OK
2. ✅ Can generate QR code (has business_id)
3. ✅ QR scan creates guest session (has business_id)
4. ✅ Menu shows items from that business only
5. ✅ Order gets business_id from guest session
6. ✅ Each business sees only their data

---

## 📊 TEST RESULTS

**Fill this out as you test:**

| Test | Status | Notes |
|------|--------|-------|
| Health Check | ⏳ |  |
| Generate QR | ⏳ | QR Token: |
| Scan QR | ⏳ | Session Token: |
| View Menu | ⏳ | Items Count: |
| Place Order | ⏳ | Order #: |
| Business Isolation | ⏳ |  |

---

## 🐛 COMMON ISSUES

### **"Cannot reach health endpoint"**
- Check if app is running: `netstat -an | findstr 9092`
- Restart application

### **"Unauthorized" on QR generation**
- Login first to get JWT token
- Verify user has OWNER role
- Check user has business_id set

### **"Table not found"**
- Create a table in database first:
```sql
INSERT INTO hotel_restaurant_tables (business_id, table_number, table_name, capacity, is_available, created_at)
VALUES (YOUR_BUSINESS_ID, 'T1', 'Table 1', 4, TRUE, NOW());
```

### **"Menu items empty"**
- Add menu items with your business_id:
```sql
INSERT INTO hotel_menu_items (business_id, category_id, name, price, is_available, created_at)
VALUES (YOUR_BUSINESS_ID, 1, 'Test Pizza', 12.99, TRUE, NOW());
```

### **"Session invalid"**
- Sessions expire after 3 hours
- Scan QR code again

---

## 🎯 WHAT TO VERIFY

### **Multi-Tenant Isolation:**
- [x] QR code has business_id
- [x] Guest session inherits business_id
- [x] Menu filtered by business_id
- [x] Orders assigned business_id
- [x] Cannot access other business data

### **QR Code Features:**
- [x] Can generate QR codes
- [x] QR codes are unique per table
- [x] Can download QR images
- [x] QR codes track scan count

### **Guest Ordering:**
- [x] No login required
- [x] Session-based authentication
- [x] Can view menu
- [x] Can place orders
- [x] Can track order status

---

## 📚 FULL TEST SUITE

**For comprehensive testing:**
- See `SYSTEM_TESTS.md`
- 9 complete tests
- Includes security tests
- Step-by-step commands

---

## 🎊 AFTER TESTING

**Once tests pass:**

1. **Generate QR codes for all tables**
2. **Download QR images**
3. **Print on stickers/cards**
4. **Place on tables**
5. **Test with real devices**
6. **Go live!**

---

## 🚀 START TESTING NOW

**Step 1:** Run health check  
**Step 2:** Login to get token  
**Step 3:** Generate QR code  
**Step 4:** Test guest ordering  
**Step 5:** Verify business isolation

**Copy-paste the PowerShell commands above!** 🎯

---

**Need help?** Check the error messages and troubleshooting section above.

**Everything working?** You have a complete multi-tenant hotel management system with contactless QR code ordering! 🎉
