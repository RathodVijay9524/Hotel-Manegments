# 🧪 MULTI-HOTEL ISOLATION TESTING

**Purpose:** Verify that each hotel owner sees ONLY their data  
**Method:** Create 3 hotels and test data isolation

---

## 📋 TEST SCENARIO

We'll create:
- **Hotel 1:** "Grand Plaza Hotel" (Owner: John)
- **Hotel 2:** "Sunset Resort" (Owner: Sarah)
- **Hotel 3:** "City Inn" (Owner: Mike)

Each hotel will have:
- Menu items
- Categories
- Tables
- Orders

**Expected Result:** Each owner sees ONLY their hotel's data

---

## 🗄️ STEP 1: CREATE TEST DATA (SQL)

Run this SQL to create 3 test hotels:

```sql
-- ==========================================
-- CREATE 3 TEST HOTEL OWNERS
-- ==========================================

-- Hotel 1: Grand Plaza Hotel (Owner: John)
INSERT INTO users (username, email, password, name, business_name, business_address, business_phone, business_type) 
VALUES (
    'john_plaza',
    'john@grandplaza.com',
    '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG13ANU8VXCrAK3c1.', -- password: "password123"
    'John Smith',
    'Grand Plaza Hotel',
    '123 Main Street, New York',
    '+1-555-0101',
    'HOTEL'
);

-- Get John's user ID
SET @john_id = LAST_INSERT_ID();

-- Assign OWNER role to John
INSERT INTO users_roles (user_id, role_id) 
VALUES (@john_id, (SELECT id FROM roles WHERE name = 'ROLE_OWNER'));

-- Hotel 2: Sunset Resort (Owner: Sarah)
INSERT INTO users (username, email, password, name, business_name, business_address, business_phone, business_type) 
VALUES (
    'sarah_sunset',
    'sarah@sunsetresort.com',
    '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG13ANU8VXCrAK3c1.', -- password: "password123"
    'Sarah Johnson',
    'Sunset Resort',
    '456 Beach Road, Miami',
    '+1-555-0202',
    'HOTEL'
);

SET @sarah_id = LAST_INSERT_ID();

INSERT INTO users_roles (user_id, role_id) 
VALUES (@sarah_id, (SELECT id FROM roles WHERE name = 'ROLE_OWNER'));

-- Hotel 3: City Inn (Owner: Mike)
INSERT INTO users (username, email, password, name, business_name, business_address, business_phone, business_type) 
VALUES (
    'mike_city',
    'mike@cityinn.com',
    '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG13ANU8VXCrAK3c1.', -- password: "password123"
    'Mike Davis',
    'City Inn',
    '789 Downtown Ave, Chicago',
    '+1-555-0303',
    'HOTEL'
);

SET @mike_id = LAST_INSERT_ID();

INSERT INTO users_roles (user_id, role_id) 
VALUES (@mike_id, (SELECT id FROM roles WHERE name = 'ROLE_OWNER'));

-- ==========================================
-- CREATE CATEGORIES FOR EACH HOTEL
-- ==========================================

-- Grand Plaza Hotel Categories
INSERT INTO hotel_categories (business_id, name, description, display_order, is_active) VALUES
(@john_id, 'Plaza Main Course', 'Main dishes at Grand Plaza', 1, true),
(@john_id, 'Plaza Desserts', 'Sweet treats at Grand Plaza', 2, true);

-- Sunset Resort Categories
INSERT INTO hotel_categories (business_id, name, description, display_order, is_active) VALUES
(@sarah_id, 'Beach Specials', 'Fresh seafood and beach fare', 1, true),
(@sarah_id, 'Tropical Drinks', 'Refreshing tropical beverages', 2, true);

-- City Inn Categories
INSERT INTO hotel_categories (business_id, name, description, display_order, is_active) VALUES
(@mike_id, 'City Classics', 'Traditional city dining', 1, true),
(@mike_id, 'Quick Bites', 'Fast and delicious', 2, true);

-- ==========================================
-- CREATE MENU ITEMS FOR EACH HOTEL
-- ==========================================

-- Grand Plaza Hotel Menu
INSERT INTO hotel_menu_items (business_id, category_id, name, description, price, is_available, is_featured, is_vegetarian) VALUES
(@john_id, (SELECT id FROM hotel_categories WHERE name = 'Plaza Main Course' AND business_id = @john_id), 'Grilled Salmon', 'Premium Atlantic salmon', 45.99, true, true, false),
(@john_id, (SELECT id FROM hotel_categories WHERE name = 'Plaza Desserts' AND business_id = @john_id), 'Chocolate Cake', 'Rich chocolate delight', 12.99, true, false, true);

-- Sunset Resort Menu
INSERT INTO hotel_menu_items (business_id, category_id, name, description, price, is_available, is_featured, is_vegetarian) VALUES
(@sarah_id, (SELECT id FROM hotel_categories WHERE name = 'Beach Specials' AND business_id = @sarah_id), 'Lobster Roll', 'Fresh Maine lobster', 38.99, true, true, false),
(@sarah_id, (SELECT id FROM hotel_categories WHERE name = 'Tropical Drinks' AND business_id = @sarah_id), 'Pina Colada', 'Classic tropical drink', 9.99, true, false, true);

-- City Inn Menu
INSERT INTO hotel_menu_items (business_id, category_id, name, description, price, is_available, is_featured, is_vegetarian) VALUES
(@mike_id, (SELECT id FROM hotel_categories WHERE name = 'City Classics' AND business_id = @mike_id), 'New York Steak', 'Prime cut steak', 52.99, true, true, false),
(@mike_id, (SELECT id FROM hotel_categories WHERE name = 'Quick Bites' AND business_id = @mike_id), 'Club Sandwich', 'Triple decker classic', 15.99, true, false, false);

-- ==========================================
-- CREATE TABLES FOR EACH HOTEL
-- ==========================================

-- Grand Plaza Tables
INSERT INTO hotel_restaurant_tables (business_id, table_number, table_name, capacity, location, is_available) VALUES
(@john_id, 'P-01', 'Plaza Table 1', 4, 'Main Dining', true),
(@john_id, 'P-02', 'Plaza Table 2', 2, 'Main Dining', true);

-- Sunset Resort Tables
INSERT INTO hotel_restaurant_tables (business_id, table_number, table_name, capacity, location, is_available) VALUES
(@sarah_id, 'S-01', 'Beach View 1', 6, 'Beachfront', true),
(@sarah_id, 'S-02', 'Beach View 2', 4, 'Beachfront', true);

-- City Inn Tables
INSERT INTO hotel_restaurant_tables (business_id, table_number, table_name, capacity, location, is_available) VALUES
(@mike_id, 'C-01', 'City Table 1', 2, 'Street View', true),
(@mike_id, 'C-02', 'City Table 2', 4, 'Street View', true);

-- ==========================================
-- CREATE SAMPLE ORDERS FOR EACH HOTEL
-- ==========================================

-- Grand Plaza Order
INSERT INTO hotel_orders (business_id, order_number, user_id, customer_name, customer_phone, order_type, status, total_amount) VALUES
(@john_id, 'ORD-PLAZA-001', @john_id, 'Guest at Plaza', '+1-555-1111', 'DINE_IN', 'PENDING', 58.98);

-- Sunset Resort Order
INSERT INTO hotel_orders (business_id, order_number, user_id, customer_name, customer_phone, order_type, status, total_amount) VALUES
(@sarah_id, 'ORD-SUNSET-001', @sarah_id, 'Guest at Sunset', '+1-555-2222', 'DINE_IN', 'PENDING', 48.98);

-- City Inn Order
INSERT INTO hotel_orders (business_id, order_number, user_id, customer_name, customer_phone, order_type, status, total_amount) VALUES
(@mike_id, 'ORD-CITY-001', @mike_id, 'Guest at City Inn', '+1-555-3333', 'DINE_IN', 'PENDING', 68.98);

-- Success message
SELECT 'Test data created successfully!' as Status,
       'John (Grand Plaza) - ID: ' as Hotel1,
       @john_id as Hotel1_ID,
       'Sarah (Sunset Resort) - ID: ' as Hotel2,
       @sarah_id as Hotel2_ID,
       'Mike (City Inn) - ID: ' as Hotel3,
       @mike_id as Hotel3_ID;
```

---

## 🧪 STEP 2: TEST DATA ISOLATION (SQL Queries)

After creating test data, verify isolation:

### **Test 1: Check Each Hotel's Data**

```sql
-- John's data (Grand Plaza)
SELECT 'GRAND PLAZA HOTEL DATA' as Test;
SELECT COUNT(*) as Categories FROM hotel_categories WHERE business_id = @john_id;
SELECT COUNT(*) as MenuItems FROM hotel_menu_items WHERE business_id = @john_id;
SELECT COUNT(*) as Tables FROM hotel_restaurant_tables WHERE business_id = @john_id;
SELECT COUNT(*) as Orders FROM hotel_orders WHERE business_id = @john_id;

-- Sarah's data (Sunset Resort)
SELECT 'SUNSET RESORT DATA' as Test;
SELECT COUNT(*) as Categories FROM hotel_categories WHERE business_id = @sarah_id;
SELECT COUNT(*) as MenuItems FROM hotel_menu_items WHERE business_id = @sarah_id;
SELECT COUNT(*) as Tables FROM hotel_restaurant_tables WHERE business_id = @sarah_id;
SELECT COUNT(*) as Orders FROM hotel_orders WHERE business_id = @sarah_id;

-- Mike's data (City Inn)
SELECT 'CITY INN DATA' as Test;
SELECT COUNT(*) as Categories FROM hotel_categories WHERE business_id = @mike_id;
SELECT COUNT(*) as MenuItems FROM hotel_menu_items WHERE business_id = @mike_id;
SELECT COUNT(*) as Tables FROM hotel_restaurant_tables WHERE business_id = @mike_id;
SELECT COUNT(*) as Orders FROM hotel_orders WHERE business_id = @mike_id;
```

**Expected:** Each hotel has 2 categories, 2 menu items, 2 tables, 1 order

### **Test 2: Verify No Cross-Business Data**

```sql
-- This should return ONLY Grand Plaza items
SELECT name, business_id FROM hotel_menu_items WHERE business_id = @john_id;
-- Should show: Grilled Salmon, Chocolate Cake

-- This should return ONLY Sunset Resort items
SELECT name, business_id FROM hotel_menu_items WHERE business_id = @sarah_id;
-- Should show: Lobster Roll, Pina Colada

-- This should return ONLY City Inn items
SELECT name, business_id FROM hotel_menu_items WHERE business_id = @mike_id;
-- Should show: New York Steak, Club Sandwich
```

---

## 🚀 STEP 3: TEST VIA API ENDPOINTS

### **Test 1: Login as Each Owner**

**A. Login as John (Grand Plaza)**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_plaza",
    "password": "password123"
  }'
```

Save the token: `JOHN_TOKEN`

**B. Login as Sarah (Sunset Resort)**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "sarah_sunset",
    "password": "password123"
  }'
```

Save the token: `SARAH_TOKEN`

**C. Login as Mike (City Inn)**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "mike_city",
    "password": "password123"
  }'
```

Save the token: `MIKE_TOKEN`

---

### **Test 2: Check Business Context**

**John's Context:**
```bash
curl http://localhost:8080/api/test/multi-tenant/context \
  -H "Authorization: Bearer JOHN_TOKEN"
```

**Expected Response:**
```json
{
  "businessId": 1,
  "isOwner": true,
  "username": "john_plaza",
  "message": "✅ You are OWNER - You see YOUR business (ID: 1)"
}
```

**Sarah's Context:**
```bash
curl http://localhost:8080/api/test/multi-tenant/context \
  -H "Authorization: Bearer SARAH_TOKEN"
```

**Expected Response:**
```json
{
  "businessId": 2,
  "isOwner": true,
  "username": "sarah_sunset",
  "message": "✅ You are OWNER - You see YOUR business (ID: 2)"
}
```

---

### **Test 3: Verify Data Isolation**

After updating your services with BusinessContextFilter:

**John Gets Menu Items (should see ONLY his items):**
```bash
curl http://localhost:8080/api/hotel/menu-items \
  -H "Authorization: Bearer JOHN_TOKEN"
```

**Expected:** Only "Grilled Salmon" and "Chocolate Cake"

**Sarah Gets Menu Items (should see ONLY her items):**
```bash
curl http://localhost:8080/api/hotel/menu-items \
  -H "Authorization: Bearer SARAH_TOKEN"
```

**Expected:** Only "Lobster Roll" and "Pina Colada"

**Mike Gets Menu Items (should see ONLY his items):**
```bash
curl http://localhost:8080/api/hotel/menu-items \
  -H "Authorization: Bearer MIKE_TOKEN"
```

**Expected:** Only "New York Steak" and "Club Sandwich"

---

### **Test 4: Cross-Access Prevention**

**Try to access Sarah's order using John's token:**

```bash
# Get Sarah's order ID first
curl http://localhost:8080/api/hotel/orders \
  -H "Authorization: Bearer SARAH_TOKEN"

# Note the order ID (let's say it's 2)

# Now try to access it with John's token
curl http://localhost:8080/api/hotel/orders/2 \
  -H "Authorization: Bearer JOHN_TOKEN"
```

**Expected:** 
- Error: "Access denied" 
- OR 404 Not Found
- John CANNOT access Sarah's order!

---

## 📊 VERIFICATION CHECKLIST

- [ ] 3 hotel owners created
- [ ] Each hotel has different data
- [ ] SQL queries show correct isolation
- [ ] Each owner can login
- [ ] Business context shows correct ID
- [ ] Each owner sees only their menu
- [ ] Each owner sees only their orders
- [ ] Cross-access is blocked
- [ ] Admin can see all data (if tested)

---

## 🎯 SUCCESS CRITERIA

✅ **Data Isolation Working:**
- John sees only Grand Plaza data
- Sarah sees only Sunset Resort data
- Mike sees only City Inn data
- No cross-business access possible

✅ **Security Working:**
- Attempting to access other business's data fails
- Error messages are appropriate
- Logs show validation working

✅ **Performance OK:**
- Queries are fast (< 100ms)
- Indexes being used
- No N+1 query problems

---

## 🚨 TROUBLESHOOTING

### **Issue: All owners see all data**
**Problem:** Services not using BusinessContextFilter  
**Fix:** Update services to filter by business_id

### **Issue: Owners can access other's data**
**Problem:** Missing validateBusinessAccess() calls  
**Fix:** Add validation in getById methods

### **Issue: Cannot create orders**
**Problem:** business_id not being set  
**Fix:** Set businessId in create methods

---

## 🎊 CLEANUP (After Testing)

To remove test data:

```sql
-- Delete test hotels and their data
DELETE FROM hotel_orders WHERE business_id IN (@john_id, @sarah_id, @mike_id);
DELETE FROM hotel_menu_items WHERE business_id IN (@john_id, @sarah_id, @mike_id);
DELETE FROM hotel_categories WHERE business_id IN (@john_id, @sarah_id, @mike_id);
DELETE FROM hotel_restaurant_tables WHERE business_id IN (@john_id, @sarah_id, @mike_id);
DELETE FROM users_roles WHERE user_id IN (@john_id, @sarah_id, @mike_id);
DELETE FROM users WHERE id IN (@john_id, @sarah_id, @mike_id);
```

---

## 📈 ADVANCED TESTS

### **Test Admin Access**

If you have an admin user:

```bash
# Login as admin
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}'

# Get all menu items (should see ALL 6 items from all hotels)
curl http://localhost:8080/api/hotel/menu-items \
  -H "Authorization: Bearer ADMIN_TOKEN"
```

**Expected:** All 6 menu items from all 3 hotels

---

## 💡 REAL-WORLD SIMULATION

1. **Login as John** - Create a new order
2. **Login as Sarah** - Try to see John's order (should fail)
3. **Login as Mike** - Create menu item
4. **Login as John** - Try to see Mike's menu (should be empty)
5. **Login as Admin** - See everything

This simulates real multi-hotel operation!

---

**Status:** 🧪 **READY FOR TESTING**  
**Run:** Execute SQL first, then test via API  
**Result:** Proof that multi-tenant isolation works!
