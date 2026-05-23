CREATE TABLE emergency_timeline (

    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    tracking_id VARCHAR(100),

    user_id BINARY(16) NOT NULL,

    event_type VARCHAR(100) NOT NULL,

    event_data VARCHAR(1000),

    created_at DATETIME NOT NULL
);