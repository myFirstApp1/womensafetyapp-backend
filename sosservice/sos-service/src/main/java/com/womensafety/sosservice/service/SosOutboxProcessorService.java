package com.womensafety.sosservice.service;

import com.womensafety.sosservice.domain.ConfirmationStatus;
import com.womensafety.sosservice.domain.SosOutbox;
import com.womensafety.sosservice.repository.SosOutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class SosOutboxProcessorService {


    private final SosRetryService sosRetryService;
    private final SosOutboxRepository sosOutboxRepository;


    @Scheduled(fixedRate = 30000)
    public void processOutbox() throws ExecutionException, InterruptedException {

        List<SosOutbox> events =
                sosOutboxRepository.findTop100ByStatusOrderByCreatedAtAsc(ConfirmationStatus.PENDING.name());

        for (SosOutbox event : events) {
            sosRetryService.sendWithRetry(event);
        }
    }


}