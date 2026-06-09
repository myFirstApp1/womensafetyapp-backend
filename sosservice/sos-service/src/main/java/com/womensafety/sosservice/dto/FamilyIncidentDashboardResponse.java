package com.womensafety.sosservice.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class FamilyIncidentDashboardResponse {

    private String trackingId;

    private Integer riskScore;

    private String status;

    private String communicationMode;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private Integer evidenceCount;
}