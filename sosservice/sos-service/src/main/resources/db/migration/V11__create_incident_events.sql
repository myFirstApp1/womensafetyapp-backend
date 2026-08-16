CREATE TABLE incident_events (

    event_id BINARY(16) NOT NULL PRIMARY KEY,

    incident_id BINARY(16) NOT NULL,

    user_id BINARY(16) NOT NULL,

    tracking_id VARCHAR(100),

    event_type VARCHAR(50) NOT NULL,

    title VARCHAR(255),

    description VARCHAR(1000),

    created_at DATETIME NOT NULL,

    CONSTRAINT fk_incident_event_incident
        FOREIGN KEY (incident_id)
        REFERENCES incidents(incident_id)

);

CREATE INDEX idx_incident_event_incident
ON incident_events(incident_id);

CREATE INDEX idx_incident_event_created
ON incident_events(created_at);