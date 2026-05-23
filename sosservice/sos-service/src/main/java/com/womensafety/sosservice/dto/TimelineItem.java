package com.womensafety.sosservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TimelineItem {

    private String eventType;

    private String eventData;

    private LocalDateTime createdAt;
}
