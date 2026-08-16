ALTER TABLE tracking_sessions
ADD CONSTRAINT fk_tracking_incident
FOREIGN KEY (incident_id)
REFERENCES incidents(incident_id);