CREATE DATABASE IF NOT EXISTS traceability_db;
USE traceability_db;

CREATE TABLE IF NOT EXISTS order_traces (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    client_id BIGINT NOT NULL,
    client_email VARCHAR(255) NOT NULL,
    previous_status VARCHAR(50),
    new_status VARCHAR(50) NOT NULL,
    employee_id BIGINT,
    employee_email VARCHAR(255),
    timestamp DATETIME NOT NULL,

    INDEX idx_order_id (order_id),
    INDEX idx_client_id (client_id),
    INDEX idx_timestamp (timestamp)
);