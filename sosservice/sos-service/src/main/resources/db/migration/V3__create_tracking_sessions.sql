CREATE TABLE tracking_sessions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,

    user_id BINARY(16) NOT NULL,

    tracking_id VARCHAR(100) NOT NULL,

    latitude DECIMAL(10,6) NOT NULL,
    longitude DECIMAL(10,6) NOT NULL,

    accuracy_meters DOUBLE,
    speed DOUBLE,

    recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    incident_id BINARY(16),

    is_active TINYINT(1) DEFAULT 1
);

CREATE INDEX idx_tracking_lookup
ON tracking_sessions(tracking_id, recorded_at);

CREATE INDEX idx_tracking_incident
ON tracking_sessions(incident_id);