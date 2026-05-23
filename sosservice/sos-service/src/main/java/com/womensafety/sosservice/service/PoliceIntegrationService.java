package com.womensafety.sosservice.service;

import com.womensafety.sosservice.domain.IncidentDispatch;
import com.womensafety.sosservice.dto.PoliceIncidentPacket;
import com.womensafety.sosservice.integration.police.PoliceGateway;
import com.womensafety.sosservice.repository.IncidentDispatchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PoliceIntegrationService {

    private final PoliceIncidentPacketService
            packetService;

    private final IncidentDispatchRepository
            repository;
    private final PoliceGateway
            policeGateway;

    public void dispatchIncident(
            String trackingId
    ) {

        PoliceIncidentPacket packet =
                packetService.buildPacket(
                        trackingId
                );

        IncidentDispatch dispatch =
                IncidentDispatch.builder()
                        .trackingId(
                                trackingId
                        )
                        .destination(
                                "POLICE_GATEWAY"
                        )
                        .dispatchedAt(
                                LocalDateTime.now()
                        )
                        .build();

        try {

            policeGateway.send(
                    packet
            );

            dispatch.setDispatchStatus(
                    "SUCCESS"
            );

            log.info(
                    "POLICE_PACKET_DISPATCHED | trackingId={}",
                    trackingId
            );

        } catch (Exception ex) {

            dispatch.setDispatchStatus(
                    "FAILED"
            );

            dispatch.setResponseMessage(
                    ex.getMessage()
            );

            log.error(
                    "POLICE_DISPATCH_FAILED | trackingId={}",
                    trackingId,
                    ex
            );
        }

        repository.save(
                dispatch
        );
    }
}