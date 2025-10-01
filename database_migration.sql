-- ========================================
-- MULTI-TENANT DATABASE MIGRATION
-- Date: October 1, 2025
-- Purpose: Add business_id to all hotel tables
-- ========================================

-- Step 1: Add business_id columns to all hotel tables
-- Default value 1 assumes first user is the existing business
-- You can update these after migration based on your data

ALTER TABLE hotel_orders ADD COLUMN business_id BIGINT NOT NULL DEFAULT 1;
ALTER TABLE hotel_menu_items ADD COLUMN business_id BIGINT NOT NULL DEFAULT 1;
ALTER TABLE hotel_categories ADD COLUMN business_id BIGINT NOT NULL DEFAULT 1;
ALTER TABLE hotel_restaurant_tables ADD COLUMN business_id BIGINT NOT NULL DEFAULT 1;
ALTER TABLE hotel_table_reservations ADD COLUMN business_id BIGINT NOT NULL DEFAULT 1;
ALTER TABLE hotel_delivery_agents ADD COLUMN business_id BIGINT NOT NULL DEFAULT 1;
ALTER TABLE hotel_delivery_tracking ADD COLUMN business_id BIGINT NOT NULL DEFAULT 1;
ALTER TABLE hotel_payments ADD COLUMN business_id BIGINT NOT NULL DEFAULT 1;
ALTER TABLE hotel_reviews ADD COLUMN business_id BIGINT NOT NULL DEFAULT 1;
ALTER TABLE hotel_order_items ADD COLUMN business_id BIGINT NOT NULL DEFAULT 1;

-- Step 2: Add indexes for performance
CREATE INDEX idx_orders_business_id ON hotel_orders(business_id);
CREATE INDEX idx_menu_items_business_id ON hotel_menu_items(business_id);
CREATE INDEX idx_categories_business_id ON hotel_categories(business_id);
CREATE INDEX idx_tables_business_id ON hotel_restaurant_tables(business_id);
CREATE INDEX idx_reservations_business_id ON hotel_table_reservations(business_id);
CREATE INDEX idx_agents_business_id ON hotel_delivery_agents(business_id);
CREATE INDEX idx_tracking_business_id ON hotel_delivery_tracking(business_id);
CREATE INDEX idx_payments_business_id ON hotel_payments(business_id);
CREATE INDEX idx_reviews_business_id ON hotel_reviews(business_id);
CREATE INDEX idx_order_items_business_id ON hotel_order_items(business_id);

-- Step 3: Add business information fields to users table
ALTER TABLE users ADD COLUMN business_name VARCHAR(200);
ALTER TABLE users ADD COLUMN business_address VARCHAR(500);
ALTER TABLE users ADD COLUMN business_phone VARCHAR(15);
ALTER TABLE users ADD COLUMN business_email VARCHAR(100);
ALTER TABLE users ADD COLUMN business_type VARCHAR(50) DEFAULT 'HOTEL';
ALTER TABLE users ADD COLUMN business_description VARCHAR(1000);

-- Step 4: Ensure OWNER role exists
INSERT INTO roles (name) VALUES ('ROLE_OWNER') 
ON DUPLICATE KEY UPDATE name='ROLE_OWNER';

-- Step 5: Update existing admin user to have OWNER role
-- Assuming user ID 1 is your main user
-- UPDATE users_roles SET role_id = (SELECT id FROM roles WHERE name = 'ROLE_OWNER') WHERE user_id = 1;

-- ========================================
-- VERIFICATION QUERIES
-- Run these to check the migration
-- ========================================

-- Check if business_id was added
-- SELECT COUNT(*) FROM hotel_orders WHERE business_id IS NOT NULL;

-- Check indexes
-- SHOW INDEX FROM hotel_orders WHERE Key_name LIKE 'idx_orders%';

-- Check user business fields
-- SELECT id, username, business_name, business_type FROM users WHERE business_name IS NOT NULL;

-- ========================================
-- POST-MIGRATION: Update business_id if needed
-- ========================================

-- If you have multiple users and want to assign specific data to specific businesses:
-- UPDATE hotel_orders SET business_id = X WHERE created_by_user = X;
-- UPDATE hotel_menu_items SET business_id = X WHERE created_by_user = X;
-- etc.

-- ========================================
-- ROLLBACK (if needed - USE WITH CAUTION!)
-- ========================================

-- To rollback (only if something went wrong):
-- ALTER TABLE hotel_orders DROP COLUMN business_id;
-- ALTER TABLE hotel_menu_items DROP COLUMN business_id;
-- ALTER TABLE hotel_categories DROP COLUMN business_id;
-- ALTER TABLE hotel_restaurant_tables DROP COLUMN business_id;
-- ALTER TABLE hotel_table_reservations DROP COLUMN business_id;
-- ALTER TABLE hotel_delivery_agents DROP COLUMN business_id;
-- ALTER TABLE hotel_delivery_tracking DROP COLUMN business_id;
-- ALTER TABLE hotel_payments DROP COLUMN business_id;
-- ALTER TABLE hotel_reviews DROP COLUMN business_id;
-- ALTER TABLE hotel_order_items DROP COLUMN business_id;

-- DROP INDEX idx_orders_business_id ON hotel_orders;
-- (repeat for all indexes)

-- ALTER TABLE users DROP COLUMN business_name;
-- ALTER TABLE users DROP COLUMN business_address;
-- ALTER TABLE users DROP COLUMN business_phone;
-- ALTER TABLE users DROP COLUMN business_email;
-- ALTER TABLE users DROP COLUMN business_type;
-- ALTER TABLE users DROP COLUMN business_description;
