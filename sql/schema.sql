-- FinPeer Database Schema
-- Run this against MySQL 8+ to create the finpeer database and tables.

CREATE DATABASE IF NOT EXISTS finpeer;
USE finpeer;

CREATE TABLE users (
    user_id       INT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    email         VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role          ENUM('ADMIN', 'BORROWER', 'LENDER') NOT NULL,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE borrower_profiles (
    user_id            INT PRIMARY KEY,
    credit_score       DECIMAL(5,2) DEFAULT 0,
    total_outstanding  DECIMAL(12,2) DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE lender_profiles (
    user_id          INT PRIMARY KEY,
    available_funds  DECIMAL(12,2) DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE loans (
    loan_id            INT PRIMARY KEY,
    borrower_id        INT NOT NULL,
    lender_id          INT,
    principal          DECIMAL(12,2) NOT NULL,
    interest_rate      DECIMAL(5,2) NOT NULL,
    term_months        INT NOT NULL,
    status             ENUM('PENDING','APPROVED','REJECTED','DISBURSED','CLOSED','DEFAULTED') DEFAULT 'PENDING',
    request_date       DATE NOT NULL,
    disbursement_date  DATE,
    schedule_created_at TIMESTAMP NULL,
    FOREIGN KEY (borrower_id) REFERENCES users(user_id),
    FOREIGN KEY (lender_id) REFERENCES users(user_id)
);

CREATE TABLE repayments (
    repayment_id  INT AUTO_INCREMENT PRIMARY KEY,
    loan_id       INT NOT NULL,
    amount_due    DECIMAL(12,2) NOT NULL,
    amount_paid   DECIMAL(12,2) DEFAULT 0,
    due_date      DATE NOT NULL,
    paid_date     DATE,
    status        ENUM('PENDING','PAID','LATE','MISSED') DEFAULT 'PENDING',
    FOREIGN KEY (loan_id) REFERENCES loans(loan_id) ON DELETE CASCADE
);

CREATE TABLE transactions (
    transaction_id  INT AUTO_INCREMENT PRIMARY KEY,
    loan_id         INT NOT NULL,
    amount          DECIMAL(12,2) NOT NULL,
    type            ENUM('DISBURSEMENT','REPAYMENT','FEE') NOT NULL,
    ts              TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (loan_id) REFERENCES loans(loan_id) ON DELETE CASCADE
);

-- Sample seed data (optional, remove for a clean start)
-- INSERT INTO users (name, email, password_hash, role) VALUES
--   ('Admin User', 'admin@finpeer.com', 'hash_here', 'ADMIN');
