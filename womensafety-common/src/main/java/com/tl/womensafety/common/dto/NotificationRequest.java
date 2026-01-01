package com.tl.womensafety.common.dto;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

/**
 * Generic notification request used by notification-service.
 */
public record NotificationRequest(
        UUID userId,
        Channel channel,
        String subject,
        String message,
        String templateId,
        Map<String, String> metadata, // e.g., placeholders, tracking ids
        String traceId
) implements Serializable {
    public enum Channel { EMAIL, SMS, PUSH, WHATSAPP  }
}