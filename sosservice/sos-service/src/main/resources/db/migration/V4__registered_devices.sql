CREATE TABLE registered_devices (

    device_id VARCHAR(100) PRIMARY KEY,

    firmware_version VARCHAR(30),

    last_heartbeat_at TIMESTAMP NULL,

    battery_level INT,

    active TINYINT(1) DEFAULT 1
);