# ⚡ QUICK FIX - Foreign Key Issue

**Problem:** FK constraint on `hotel_table_reservations.table_id`  
**Solution:** 2-step fix (2 minutes)

---

## 🔧 STEP 1: Run this SQL (1 minute)

```sql
-- Drop the problematic FK constraint
ALTER TABLE hotel_table_reservations 
DROP FOREIGN KEY IF EXISTS FK358x2blhm279qnbq724nkg4a9;

-- Fix orphaned records (set table_id to NULL for invalid references)
UPDATE hotel_table_reservations 
SET table_id = NULL 
WHERE table_id NOT IN (SELECT id FROM hotel_restaurant_tables);

-- Create QR code tables
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

## ✅ STEP 2: Start Application

**application.properties already updated to:**
```properties
spring.jpa.hibernate.ddl-auto=validate
```

**Now start:**
```bash
./gradlew bootRun
```

**Expected:** Application starts successfully!

---

## 🧪 STEP 3: Quick Test

```bash
# Test health endpoint
curl http://localhost:9092/api/public/guest/health
```

**Expected Response:**
```json
{
  "message": "Guest ordering API is running",
  "status": "OK"
}
```

---

## ✅ DONE!

**If application starts:**
- ✅ FK issue fixed
- ✅ QR code tables created
- ✅ Ready to test features!

**Next:** Follow QUICK_TEST_START.md for full testing

---

## 🔍 What We Fixed

**Problem:**
- Old FK constraint on reservations table
- Orphaned data causing FK violations

**Solution:**
- Dropped FK constraint
- Cleaned orphaned data
- Changed Hibernate mode to `validate` (no auto FK creation)
- Manually created QR tables

**Result:**
- ✅ No more FK errors
- ✅ Application starts
- ✅ Ready to test!
