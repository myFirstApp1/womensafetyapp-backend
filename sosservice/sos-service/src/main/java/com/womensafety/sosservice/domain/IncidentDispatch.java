package com.womensafety.sosservice.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "incident_dispatch")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncidentDispatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String trackingId;

    private String destination;

    private String dispatchStatus;

    private String responseCode;

    @Column(length = 2000)
    private String responseMessage;

    private LocalDateTime dispatchedAt;
}
