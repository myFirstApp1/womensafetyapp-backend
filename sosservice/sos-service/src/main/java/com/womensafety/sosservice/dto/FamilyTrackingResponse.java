package com.womensafety.sosservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class FamilyTrackingResponse {

    private String trackingId;

    private String status;

    private Integer riskScore;

    private Double latitude;

    private Double longitude;

    private LocalDateTime lastPingTime;

    private List<TimelineItem> timeline;
}