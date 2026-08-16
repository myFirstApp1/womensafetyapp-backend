package com.womensafety.sosservice.service.sos;

import com.womensafety.sosservice.domain.enums.OutboxStatus;
import com.womensafety.sosservice.domain.SosOutbox;
import com.womensafety.sosservice.service.communication.NotificationService;
import com.womensafety.sosservice.repository.SosOutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
@Slf4j
public class SosRetryService {

    private final SosOutboxRepository sosOutboxRepository;
    private final NotificationService notificationService;
    
    @Retryable(
            value = Exception.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public void sendWithRetry(SosOutbox event)
            throws Exception {
        event.setRetryCount(event.getRetryCount() + 1);
        sosOutboxRepository.save(event);
        notificationService.sendAutomaticSos(
                event.getUserId(),
                event.getLocation(),
                null
        );
        event.setStatus(OutboxStatus.PUBLISHED);
        sosOutboxRepository.save(event);
    }

    @Recover
    public void recover(Exception e, SosOutbox event) {
        log.error(
                "SOS FAILED after retries | userId={}",
                event.getUserId(),
                e
        );
        try {
            notificationService.sendToDLT(
                    event.getUserId(),
                    event.getLocation()
            );
            event.setStatus(OutboxStatus.DLT);
        } catch (Exception dltException) {
            log.error(
                    "DLT_SEND_FAILED | eventId={}",
                    event.getEventId(),
                    dltException
            );
            event.setStatus(OutboxStatus.FAILED);
        }
        event.setRetryCount(
                event.getRetryCount() + 1
        );
        event.setFailureReason(
                e.getMessage()
        );
        sosOutboxRepository.save(event);
    }
}
