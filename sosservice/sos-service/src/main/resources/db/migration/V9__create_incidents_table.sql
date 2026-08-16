CREATE TABLE incidents (

    incident_id BINARY(16) NOT NULL PRIMARY KEY,

    user_id BINARY(16) NOT NULL,

    tracking_id VARCHAR(100),

    trigger_type VARCHAR(30) NOT NULL,

    status VARCHAR(30) NOT NULL,

    risk_score INT,

    latitude DECIMAL(10,6),

    longitude DECIMAL(10,6),

    created_at DATETIME NOT NULL,

    warning_at DATETIME,

    danger_at DATETIME,

    tracking_started_at DATETIME,

    resolved_at DATETIME,
    incident_source VARCHAR(30),

    closed_at DATETIME

);
CREATE INDEX idx_incident_user
ON incidents(user_id);

CREATE INDEX idx_incident_tracking
ON incidents(tracking_id);

CREATE INDEX idx_incident_status
ON incidents(status);

CREATE INDEX idx_incident_created
ON incidents(created_at);