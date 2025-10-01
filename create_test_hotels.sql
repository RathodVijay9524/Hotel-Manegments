-- ==========================================
-- MULTI-HOTEL ISOLATION TEST DATA
-- Creates 3 hotels with complete data
-- ==========================================

-- Hotel 1: Grand Plaza Hotel (Owner: John)
INSERT INTO users (username, email, password, name, business_name, business_address, business_phone, business_type) 
VALUES (
    'john_plaza',
    'john@grandplaza.com',
    '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG13ANU8VXCrAK3c1.',
    'John Smith',
    'Grand Plaza Hotel',
    '123 Main Street, New York',
    '+1-555-0101',
    'HOTEL'
);

SET @john_id = LAST_INSERT_ID();
INSERT INTO users_roles (user_id, role_id) VALUES (@john_id, (SELECT id FROM roles WHERE name = 'ROLE_OWNER'));

-- Hotel 2: Sunset Resort (Owner: Sarah)
INSERT INTO users (username, email, password, name, business_name, business_address, business_phone, business_type) 
VALUES (
    'sarah_sunset',
    'sarah@sunsetresort.com',
    '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG13ANU8VXCrAK3c1.',
    'Sarah Johnson',
    'Sunset Resort',
    '456 Beach Road, Miami',
    '+1-555-0202',
    'HOTEL'
);

SET @sarah_id = LAST_INSERT_ID();
INSERT INTO users_roles (user_id, role_id) VALUES (@sarah_id, (SELECT id FROM roles WHERE name = 'ROLE_OWNER'));

-- Hotel 3: City Inn (Owner: Mike)
INSERT INTO users (username, email, password, name, business_name, business_address, business_phone, business_type) 
VALUES (
    'mike_city',
    'mike@cityinn.com',
    '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG13ANU8VXCrAK3c1.',
    'Mike Davis',
    'City Inn',
    '789 Downtown Ave, Chicago',
    '+1-555-0303',
    'HOTEL'
);

SET @mike_id = LAST_INSERT_ID();
INSERT INTO users_roles (user_id, role_id) VALUES (@mike_id, (SELECT id FROM roles WHERE name = 'ROLE_OWNER'));

-- Categories for each hotel
INSERT INTO hotel_categories (business_id, name, description, display_order, is_active) VALUES
(@john_id, 'Plaza Main Course', 'Main dishes at Grand Plaza', 1, true),
(@john_id, 'Plaza Desserts', 'Sweet treats at Grand Plaza', 2, true),
(@sarah_id, 'Beach Specials', 'Fresh seafood and beach fare', 1, true),
(@sarah_id, 'Tropical Drinks', 'Refreshing tropical beverages', 2, true),
(@mike_id, 'City Classics', 'Traditional city dining', 1, true),
(@mike_id, 'Quick Bites', 'Fast and delicious', 2, true);

-- Menu items for each hotel
INSERT INTO hotel_menu_items (business_id, category_id, name, description, price, is_available, is_featured, is_vegetarian) VALUES
(@john_id, (SELECT id FROM hotel_categories WHERE name = 'Plaza Main Course' AND business_id = @john_id), 'Grilled Salmon', 'Premium Atlantic salmon', 45.99, true, true, false),
(@john_id, (SELECT id FROM hotel_categories WHERE name = 'Plaza Desserts' AND business_id = @john_id), 'Chocolate Cake', 'Rich chocolate delight', 12.99, true, false, true),
(@sarah_id, (SELECT id FROM hotel_categories WHERE name = 'Beach Specials' AND business_id = @sarah_id), 'Lobster Roll', 'Fresh Maine lobster', 38.99, true, true, false),
(@sarah_id, (SELECT id FROM hotel_categories WHERE name = 'Tropical Drinks' AND business_id = @sarah_id), 'Pina Colada', 'Classic tropical drink', 9.99, true, false, true),
(@mike_id, (SELECT id FROM hotel_categories WHERE name = 'City Classics' AND business_id = @mike_id), 'New York Steak', 'Prime cut steak', 52.99, true, true, false),
(@mike_id, (SELECT id FROM hotel_categories WHERE name = 'Quick Bites' AND business_id = @mike_id), 'Club Sandwich', 'Triple decker classic', 15.99, true, false, false);

-- Tables for each hotel
INSERT INTO hotel_restaurant_tables (business_id, table_number, table_name, capacity, location, is_available) VALUES
(@john_id, 'P-01', 'Plaza Table 1', 4, 'Main Dining', true),
(@john_id, 'P-02', 'Plaza Table 2', 2, 'Main Dining', true),
(@sarah_id, 'S-01', 'Beach View 1', 6, 'Beachfront', true),
(@sarah_id, 'S-02', 'Beach View 2', 4, 'Beachfront', true),
(@mike_id, 'C-01', 'City Table 1', 2, 'Street View', true),
(@mike_id, 'C-02', 'City Table 2', 4, 'Street View', true);

-- Sample orders
INSERT INTO hotel_orders (business_id, order_number, user_id, customer_name, customer_phone, order_type, status, total_amount) VALUES
(@john_id, 'ORD-PLAZA-001', @john_id, 'Guest at Plaza', '+1-555-1111', 'DINE_IN', 'PENDING', 58.98),
(@sarah_id, 'ORD-SUNSET-001', @sarah_id, 'Guest at Sunset', '+1-555-2222', 'DINE_IN', 'PENDING', 48.98),
(@mike_id, 'ORD-CITY-001', @mike_id, 'Guest at City Inn', '+1-555-3333', 'DINE_IN', 'PENDING', 68.98);

-- Summary
SELECT '✅ TEST DATA CREATED SUCCESSFULLY!' as Status;
SELECT CONCAT('Grand Plaza Hotel - User ID: ', @john_id, ' (john_plaza / password123)') as Hotel1;
SELECT CONCAT('Sunset Resort - User ID: ', @sarah_id, ' (sarah_sunset / password123)') as Hotel2;
SELECT CONCAT('City Inn - User ID: ', @mike_id, ' (mike_city / password123)') as Hotel3;
SELECT '⚡ Now test API endpoints with each user!' as NextStep;
