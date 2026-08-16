package com.womensafety.sosservice.service.sos;

import com.womensafety.sosservice.domain.enums.OutboxStatus;
import com.womensafety.sosservice.domain.SosOutbox;
import com.womensafety.sosservice.repository.SosOutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SosOutboxProcessorService {


    private final SosRetryService sosRetryService;
    private final SosOutboxRepository sosOutboxRepository;


    @Scheduled(fixedRate = 30000)
    public void processOutbox() {
        List<SosOutbox> events =
                sosOutboxRepository
                        .findTop100ByStatusInOrderByCreatedAtAsc(
                                List.of(
                                        OutboxStatus.PENDING,
                                        OutboxStatus.FAILED
                                )
                        );

        for (SosOutbox event : events) {
            try {

                sosRetryService.sendWithRetry(event);

            } catch (Exception e) {

                log.error(
                        "OUTBOX_PROCESSING_FAILED | eventId={}",
                        event.getEventId(),
                        e
                );
            }
        }
    }
}
