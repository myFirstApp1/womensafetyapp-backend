package com.womensafety.sosservice.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActiveTrackingResponse {

    private boolean active;

    private String trackingId;

}