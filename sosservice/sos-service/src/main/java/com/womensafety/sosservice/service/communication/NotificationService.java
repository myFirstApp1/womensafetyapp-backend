package com.womensafety.sosservice.service.communication;

import java.util.UUID;

/**
 * Service for notification delivery through various channels.
 * Abstracts the notification mechanism from controllers and services.
 */
public interface NotificationService {

    /**
     * Send an automatic SOS notification for a user at a given location.
     * Channel is optional and can be used to route to different providers (e.g. whatsapp).
     */
    void sendAutomaticSos(
            UUID userId,
            String location,
            String channel
    ) throws Exception;

    /**
     * Send to dead-letter topic or fallback for failed SOS deliveries.
     */
    void sendToDLT(
            UUID userId,
            String location
    ) throws Exception;

}
