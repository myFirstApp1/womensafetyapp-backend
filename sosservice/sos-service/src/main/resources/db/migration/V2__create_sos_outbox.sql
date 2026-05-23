CREATE TABLE sos_outbox (

    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    user_id BINARY(16) NOT NULL,

    tracking_id VARCHAR(100),

    location VARCHAR(255),

    status VARCHAR(50),

    retry_count INT DEFAULT 0,

    event_id VARCHAR(100) UNIQUE,

    failure_reason VARCHAR(500),

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    INDEX idx_tracking_id (tracking_id)

);