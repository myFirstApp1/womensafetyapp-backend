package com.womensafety.sosservice.service.timeline;

import com.womensafety.sosservice.domain.Incident;
import com.womensafety.sosservice.domain.enums.IncidentEventType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmergencyTimelineOrchestratorService {

    private final IncidentTimelineService incidentTimelineService;

    /// incident created
    public void incidentCreated(Incident incident) {

        incidentTimelineService.addEvent(
                incident,
                IncidentEventType.INCIDENT_CREATED,
                "Incident Created",
                "AI detected suspicious activity"
        );
    }


    public void statusChanged(
            Incident incident,
            String newStatus
    ) {

        incidentTimelineService.addEvent(
                incident,
                IncidentEventType.STATUS_CHANGED,
                "Status Updated",
                "Incident status changed to " + newStatus
        );
    }

    /// RiskScore AI
    public void riskScoreCalculated(
            Incident incident,
            int riskScore
    ) {
        incidentTimelineService.addEvent(
                incident,
                IncidentEventType.RISK_SCORE_CALCULATED,
                "Risk Score Calculated",
                "Calculated risk score: " + riskScore
        );
    }

    /// GPS
    public void gpsAcquired(
            Incident incident
    ) {

        incidentTimelineService.addEvent(
                incident,
                IncidentEventType.GPS_ACQUIRED,
                "GPS Acquired",
                "Live location obtained"
        );
    }

    public void gpsLost(
            Incident incident
    ) {

        incidentTimelineService.addEvent(
                incident,
                IncidentEventType.GPS_LOST,
                "GPS Lost",
                "Unable to obtain device location"
        );
    }

    /// Tracking
    public void trackingStarted(
            Incident incident
    ) {

        incidentTimelineService.addEvent(
                incident,
                IncidentEventType.TRACKING_STARTED,
                "Tracking Started",
                "Live tracking has started"
        );
    }

    public void trackingStopped(
            Incident incident
    ) {

        incidentTimelineService.addEvent(
                incident,
                IncidentEventType.TRACKING_STOPPED,
                "Tracking Stopped",
                "Live tracking has stopped"
        );
    }

    /// Family
    public void familyNotified(
            Incident incident
    ) {

        incidentTimelineService.addEvent(
                incident,
                IncidentEventType.FAMILY_NOTIFIED,
                "Family Notified",
                "Emergency contacts have been informed"
        );
    }

    /// Sms and voice call
    public void smsSent(
            Incident incident
    ) {

        incidentTimelineService.addEvent(
                incident,
                IncidentEventType.SMS_SENT,
                "SMS Sent",
                "Emergency SMS delivered"
        );
    }

    public void voiceCallSent(
            Incident incident
    ) {

        incidentTimelineService.addEvent(
                incident,
                IncidentEventType.VOICE_CALL_SENT,
                "Voice Call Initiated",
                "Emergency voice call initiated"
        );
    }

    /// push notification
    public void pushNotificationSent(
            Incident incident
    ) {

        incidentTimelineService.addEvent(
                incident,
                IncidentEventType.PUSH_NOTIFICATION_SENT,
                "Push Notification",
                "Emergency push notification sent"
        );
    }

    /// Police packet and dispatch
    public void policePacketCreated(
            Incident incident
    ) {

        incidentTimelineService.addEvent(
                incident,
                IncidentEventType.POLICE_PACKET_CREATED,
                "Police Packet Created",
                "Emergency packet prepared"
        );
    }

    public void policeDispatched(
            Incident incident
    ) {

        incidentTimelineService.addEvent(
                incident,
                IncidentEventType.POLICE_DISPATCHED,
                "Police Dispatch",
                "Incident forwarded to police integration"
        );
    }

    /// Incident Resolved and closed
    public void incidentResolved(
            Incident incident
    ) {

        incidentTimelineService.addEvent(
                incident,
                IncidentEventType.INCIDENT_RESOLVED,
                "Incident Resolved",
                "Emergency has been resolved"
        );
    }
    public void incidentClosed(
            Incident incident
    ) {

        incidentTimelineService.addEvent(
                incident,
                IncidentEventType.INCIDENT_CLOSED,
                "Incident Closed",
                "Emergency incident closed"
        );
    }

}
