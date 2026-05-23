CREATE TABLE incident_dispatch (

    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    tracking_id VARCHAR(100),

    destination VARCHAR(100),

    dispatch_status VARCHAR(50),

    response_code VARCHAR(100),

    response_message VARCHAR(2000),

    dispatched_at DATETIME
);