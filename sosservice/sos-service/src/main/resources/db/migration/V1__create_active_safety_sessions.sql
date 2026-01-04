-- ==============================
-- ACTIVE SAFETY SESSIONS TABLE
-- ==============================

CREATE TABLE active_safety_sessions (
    user_id BINARY(16) PRIMARY KEY,

    device_id VARCHAR(100),

    last_ping_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_protected TINYINT(1) NOT NULL DEFAULT 0,

    battery_level INT,

    last_latitude DECIMAL(10,6),
    last_longitude DECIMAL(10,6),

    emergency_triggered TINYINT(1) DEFAULT 0,
    emergency_contact_notified TINYINT(1) DEFAULT 0,

    session_start_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ==============================
-- INDEXES
-- ==============================

CREATE INDEX idx_heartbeat_check
ON active_safety_sessions(is_protected, last_ping_time);
