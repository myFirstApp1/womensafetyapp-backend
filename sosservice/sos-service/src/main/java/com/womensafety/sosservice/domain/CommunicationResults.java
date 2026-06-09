package com.womensafety.sosservice.domain;

import com.womensafety.sosservice.domain.enums.CommunicationMode;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommunicationResults {

    private boolean success;

    private boolean acknowledged;

    private CommunicationMode mode;

    private String reason;
}