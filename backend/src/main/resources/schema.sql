-- ==============================================================
-- J. Sethuraman Portfolio Database Schema (MySQL)
-- Safe migrations: uses CREATE TABLE IF NOT EXISTS
-- ==============================================================

-- 1. Contact messages submitted from the portfolio contact form
CREATE TABLE IF NOT EXISTS contact_messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL,
    subject VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,
    submitted_at DATETIME NOT NULL,
    ip_hash VARCHAR(64) NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    INDEX idx_contact_submitted_at (submitted_at DESC),
    INDEX idx_contact_is_read (is_read)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. Daily visitor statistics (aggregated, privacy-preserving)
CREATE TABLE IF NOT EXISTS visitor_stats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    visit_date DATE NOT NULL,
    total_visits BIGINT NOT NULL DEFAULT 0,
    unique_visitors BIGINT NOT NULL DEFAULT 0,
    UNIQUE KEY uk_visitor_stats_date (visit_date),
    INDEX idx_visitor_stats_date (visit_date DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. Daily visitor fingerprints for deduplication (SHA-256 hashes only, NO raw IPs)
CREATE TABLE IF NOT EXISTS visitor_fingerprints (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    fingerprint_hash VARCHAR(64) NOT NULL,
    visit_date DATE NOT NULL,
    UNIQUE KEY uk_fingerprint_date (fingerprint_hash, visit_date),
    INDEX idx_fingerprint_date (visit_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
