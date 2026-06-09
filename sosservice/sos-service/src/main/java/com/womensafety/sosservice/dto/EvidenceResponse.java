package com.womensafety.sosservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class EvidenceResponse {

    private String evidenceId;

    private String fileType;

    private String storageUrl;

    private String hashValue;

    private LocalDateTime uploadedAt;

    private String uploadedBy;
}