package com.womensafety.sosservice.domain.enums;

public enum IncidentEventType {

    // Incident
    INCIDENT_CREATED,
    STATUS_CHANGED,
    INCIDENT_RESOLVED,
    INCIDENT_CLOSED,

    // AI
    RISK_SCORE_CALCULATED,

    // GPS
    GPS_ACQUIRED,
    GPS_LOST,

    // Tracking
    TRACKING_STARTED,
    TRACKING_STOPPED,

    // Family
    FAMILY_NOTIFIED,

    // Communication
    SMS_SENT,
    VOICE_CALL_SENT,
    PUSH_NOTIFICATION_SENT,

    // Police
    POLICE_PACKET_CREATED,
    POLICE_DISPATCHED,

    // Evidence
    EVIDENCE_CAPTURED
}
