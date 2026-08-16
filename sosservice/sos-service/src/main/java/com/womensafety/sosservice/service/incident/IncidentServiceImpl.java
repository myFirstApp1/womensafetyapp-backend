package com.womensafety.sosservice.service.incident;

import com.womensafety.sosservice.domain.Incident;
import com.womensafety.sosservice.domain.enums.IncidentEventType;
import com.womensafety.sosservice.domain.enums.IncidentStatus;
import com.womensafety.sosservice.domain.enums.IncidentTriggerType;
import com.womensafety.sosservice.dto.IncidentEventResponse;
import com.womensafety.sosservice.dto.IncidentResponse;
import com.womensafety.sosservice.exception.IncidentNotFoundException;
import com.womensafety.sosservice.mapper.IncidentMapper;
import com.womensafety.sosservice.repository.IncidentRepository;
import com.womensafety.sosservice.service.timeline.IncidentTimelineService;
import com.womensafety.sosservice.service.timeline.EmergencyTimelineOrchestratorService;
import com.womensafety.sosservice.statemachine.IncidentStateMachineService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class IncidentServiceImpl implements IncidentService{

    private final IncidentRepository incidentRepository;
    private final IncidentMapper incidentMapper;
    private final IncidentStateMachineService incidentStateMachineService;
    private final IncidentTimelineService incidentTimelineService;
    private final EmergencyTimelineOrchestratorService timelineOrchestrator;

    @Override
    @Transactional
    public IncidentResponse createIncident(
            UUID userId,
            String trackingId,
            IncidentTriggerType triggerType,
            Integer riskScore,
            BigDecimal latitude,
            BigDecimal longitude,
            String incidentSource
    ) {

        log.info(
                "INCIDENT_CREATE | userId={} | trigger={} | source={}",
                userId,
                triggerType,
                incidentSource
        );

        Incident incident = Incident.builder()
                .incidentId(UUID.randomUUID())
                .userId(userId)
                .trackingId(trackingId)
                .triggerType(triggerType)
                .status(IncidentStatus.CREATED)
                .riskScore(riskScore)
                .latitude(latitude)
                .longitude(longitude)
                .incidentSource(incidentSource)
                .createdAt(LocalDateTime.now())
                .build();

        Incident saved = save(incident);

        timelineOrchestrator.incidentCreated(saved);



        log.info(
                "INCIDENT_CREATED | incidentId={}",
                saved.getIncidentId()
        );

        return incidentMapper.toResponse(saved);
    }
    private Incident getIncidentOrThrow(UUID incidentId) {
        return incidentRepository
                .findById(incidentId)
                .orElseThrow(() ->
                        new IncidentNotFoundException(incidentId)
                );
    }

    private Incident save(Incident incident) {

        return incidentRepository.save(incident);

    }

    private void updateTimestamp(
            Incident incident,
            IncidentStatus status
    ) {

        LocalDateTime now = LocalDateTime.now();

        switch (status) {

            case WARNING ->
                    incident.setWarningAt(now);

            case IN_DANGER ->
                    incident.setDangerAt(now);

            case TRACKING ->
                    incident.setTrackingStartedAt(now);

            case RESOLVED ->
                    incident.setResolvedAt(now);

            case CLOSED ->
                    incident.setClosedAt(now);

            default -> {
            }
        }

    }

    @Override
    public IncidentResponse getIncident(UUID incidentId) {
        Incident incident =
                getIncidentOrThrow(incidentId);

        return incidentMapper.toResponse(incident);

    }

    @Override
    public Incident findById(UUID incidentId) {

        return incidentRepository.findById(incidentId)
                .orElseThrow(() ->
                        new IncidentNotFoundException(incidentId));
    }

    @Override
    public List<IncidentResponse> getUserIncidents(UUID userId) {
        return incidentRepository
                .findTop50ByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(incidentMapper::toResponse)
                .toList();

    }

    @Override
    public IncidentResponse getActiveIncident(UUID userId) {
        Incident incident = incidentRepository
                .findTopByUserIdAndStatusInOrderByCreatedAtDesc(
                        userId,
                        List.of(
                                IncidentStatus.CREATED,
                                IncidentStatus.WARNING,
                                IncidentStatus.IN_DANGER,
                                IncidentStatus.TRACKING
                        )
                )
                .orElseThrow(() ->
                        new IncidentNotFoundException(userId));

        return incidentMapper.toResponse(incident);
    }

    @Override
    @Transactional
    public IncidentResponse updateStatus(UUID incidentId, IncidentStatus newStatus) {
        Incident incident =
                getIncidentOrThrow(incidentId);

        incidentStateMachineService.transition(
                incident,
                newStatus
        );

        updateTimestamp(
                incident,
                newStatus
        );

        Incident saved =
                save(incident);

        timelineOrchestrator.statusChanged(
                saved,
                newStatus.name()
        );

        log.info(
                "INCIDENT_STATUS_UPDATED | incidentId={} | status={}",
                incidentId,
                newStatus
        );

        return incidentMapper.toResponse(saved);
    }

    @Override
    public boolean existsActiveIncident(UUID userId) {
        return incidentRepository
                .findTopByUserIdAndStatusInOrderByCreatedAtDesc(
                        userId,
                        List.of(
                                IncidentStatus.CREATED,
                                IncidentStatus.WARNING,
                                IncidentStatus.IN_DANGER,
                                IncidentStatus.TRACKING
                        )
                )
                .isPresent();
    }

    @Override
    public List<IncidentEventResponse> getTimeline(UUID incidentId) {

        getIncidentOrThrow(incidentId);

        return incidentTimelineService.getTimeline(incidentId);

    }

}