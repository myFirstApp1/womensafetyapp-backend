package com.womensafety.sosservice.service;

import com.womensafety.sosservice.domain.OutboxStatus;
import com.womensafety.sosservice.domain.SosOutbox;
import com.womensafety.sosservice.kafka.NotificationProducer;
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
    private final NotificationProducer notificationProducer;
    @Retryable(
            value = Exception.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public void sendWithRetry(SosOutbox event)
            throws ExecutionException, InterruptedException {
        event.setRetryCount(event.getRetryCount() + 1);
        sosOutboxRepository.save(event);
        notificationProducer.sendAutomaticSOS(
                event.getUserId().toString(),
                event.getLocation()
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
            notificationProducer.sendToDLT(
                    event.getUserId().toString(),
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