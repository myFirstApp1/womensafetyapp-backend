CREATE TABLE evidence_records (

    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    evidence_id VARCHAR(100) NOT NULL UNIQUE,

    tracking_id VARCHAR(100) NOT NULL,

    file_type VARCHAR(50) NOT NULL,

    storage_url VARCHAR(1000) NOT NULL,

    hash_value VARCHAR(255),

    uploaded_at DATETIME NOT NULL,

    uploaded_by VARCHAR(100),

    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,

    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_tracking_id (tracking_id),

    INDEX idx_file_type (file_type),

    INDEX idx_uploaded_at (uploaded_at)

);