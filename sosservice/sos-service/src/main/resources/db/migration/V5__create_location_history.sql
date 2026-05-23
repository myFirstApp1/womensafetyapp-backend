CREATE TABLE location_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    user_id BINARY(16) NOT NULL,

    device_id VARCHAR(100) NOT NULL,

    latitude DECIMAL(10,6) NOT NULL,

    longitude DECIMAL(10,6) NOT NULL,

    captured_at DATETIME NOT NULL
);