-- =====================================================
-- CREATE ALL TABLES FOR HOTEL MANAGEMENT SYSTEM
-- Run this complete SQL script
-- =====================================================

-- Use the database
USE `user-master`;

-- =====================================================
-- RESTAURANT TABLES
-- =====================================================

CREATE TABLE IF NOT EXISTS hotel_restaurant_tables (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    business_id BIGINT,
    table_number VARCHAR(20) NOT NULL,
    table_name VARCHAR(100),
    capacity INT NOT NULL,
    is_available BOOLEAN NOT NULL DEFAULT TRUE,
    location VARCHAR(100),
    qr_code_url VARCHAR(500),
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    INDEX idx_table_business (business_id),
    INDEX idx_table_available (is_available)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- RESERVATIONS
-- =====================================================

CREATE TABLE IF NOT EXISTS hotel_table_reservations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    business_id BIGINT,
    user_id BIGINT,
    table_id BIGINT,
    reservation_date DATE NOT NULL,
    reservation_time TIME NOT NULL,
    guest_name VARCHAR(100) NOT NULL,
    guest_phone VARCHAR(20) NOT NULL,
    guest_email VARCHAR(100),
    party_size INT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    special_requests TEXT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    INDEX idx_reservation_business (business_id),
    INDEX idx_reservation_date (reservation_date),
    INDEX idx_reservation_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- QR CODE SYSTEM
-- =====================================================

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- VERIFICATION
-- =====================================================

-- Show created tables
SHOW TABLES LIKE 'hotel_%';
SHOW TABLES LIKE 'qr_codes';
SHOW TABLES LIKE 'guest_sessions';

-- =====================================================
-- DONE!
-- All tables created successfully!
-- Now start the application: ./gradlew bootRun
-- =====================================================
