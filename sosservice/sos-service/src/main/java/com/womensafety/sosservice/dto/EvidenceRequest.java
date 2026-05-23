package com.womensafety.sosservice.dto;

import com.womensafety.sosservice.domain.EvidenceType;
import lombok.Data;

@Data
public class EvidenceRequest {

    private String trackingId;

    private EvidenceType fileType;

    private String storageUrl;

    private String hashValue;
}