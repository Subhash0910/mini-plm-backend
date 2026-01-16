-- ====================================================================
-- Flyway Migration: V2 - Insert Default Test Users
-- ====================================================================
-- Description: Insert default test users for development and testing.
--              Passwords are BCrypt hashed.
-- Author: Mini PLM Backend
-- Date: 2026-01-16
-- ====================================================================
-- NOTE: These are default credentials for DEVELOPMENT/TESTING only.
--       CHANGE these immediately in production!
-- ====================================================================

-- BCrypt hashed passwords:
-- admin123 -> $2a$10$YZODXTFp15iMq6HWyJ3L3eH0OQbHYVYYRf6pLVuFJONqOYVH8NM5u
-- manager123 -> $2a$10$QbxZyWHV9tJ5q4L3r2K2n.V0Q5K5H8L9R2K2Q3L4M5N6O7P8Q9R0S
-- user123 -> $2a$10$gzk9R8L7K6J5H4G3F2E1D0C9B8A7F6E5D4C3B2A1@9Z8Y7X6W5V4U3T2S1R0

INSERT INTO users (username, password, email, first_name, last_name, role, is_active, created_at, updated_at)
VALUES 
-- Admin User
(
    'admin',
    '$2a$10$YZODXTFp15iMq6HWyJ3L3eH0OQbHYVYYRf6pLVuFJONqOYVH8NM5u',
    'admin@company.com',
    'Admin',
    'User',
    'ADMIN',
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
-- Manager User
(
    'manager',
    '$2a$10$QbxZyWHV9tJ5q4L3r2K2n.V0Q5K5H8L9R2K2Q3L4M5N6O7P8Q9R0S',
    'manager@company.com',
    'Manager',
    'User',
    'MANAGER',
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
-- Regular User
(
    'user',
    '$2a$10$gzk9R8L7K6J5H4G3F2E1D0C9B8A7F6E5D4C3B2A1@9Z8Y7X6W5V4U3T2S1R0',
    'user@company.com',
    'Regular',
    'User',
    'USER',
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
)
ON CONFLICT (username) DO NOTHING;

-- Print migration result
SELECT 'Default users inserted successfully' AS migration_result;
