CREATE TABLE active_safety_sessions (

    user_id BINARY(16) PRIMARY KEY,

    device_id VARCHAR(100),

    last_ping_time TIMESTAMP NOT NULL
    DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP,

    battery_level INT,

    last_latitude DECIMAL(10,6),

    last_longitude DECIMAL(10,6),

    -- DEVICE INTELLIGENCE

    last_heart_rate INT,

    movement_score INT,

    is_device_worn TINYINT(1) DEFAULT 1,

    last_bluetooth_seen_at TIMESTAMP NULL,

    -- STATE MACHINE

    status VARCHAR(30) DEFAULT 'ACTIVE',

    pause_type VARCHAR(30) NULL,

    confirmation_status VARCHAR(30) DEFAULT 'NONE',

    warning_triggered_at TIMESTAMP NULL,

    auto_resume_at TIMESTAMP NULL,

    -- TRACKING

    tracking_id VARCHAR(100),

    -- EMERGENCY FLAGS

    emergency_triggered TINYINT(1) DEFAULT 0,

    emergency_contact_notified TINYINT(1) DEFAULT 0,

    version BIGINT DEFAULT 0,
    risk_score INT DEFAULT 0,
    communication_mode VARCHAR(30) DEFAULT 'PHONE_BLUETOOTH',

    communication_failure_count INT DEFAULT 0,

    last_communication_attempt TIMESTAMP NULL,
    last_gps_status VARCHAR(50),
    last_off_body_event VARCHAR(50),

    -- SESSION

    session_start_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_heartbeat_check
ON active_safety_sessions(status, last_ping_time);

CREATE INDEX idx_tracking_id
ON active_safety_sessions(tracking_id);