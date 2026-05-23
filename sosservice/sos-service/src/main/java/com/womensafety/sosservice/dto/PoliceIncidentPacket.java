package com.womensafety.sosservice.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PoliceIncidentPacket {

    private String trackingId;

    private UUID userId;

    private Integer riskScore;

    private String status;

    private String communicationMode;

    private Double latitude;

    private Double longitude;

    private LocalDateTime incidentTime;

    private List<String> timelineEvents;

    private List<String> evidenceUrls;
}