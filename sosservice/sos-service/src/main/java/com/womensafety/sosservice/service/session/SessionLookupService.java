package com.womensafety.sosservice.service.session;

import com.womensafety.sosservice.domain.ActiveSafetySession;
import com.womensafety.sosservice.exception.SessionNotFoundException;
import com.womensafety.sosservice.repository.ActiveSafetySessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionLookupService {

    private final ActiveSafetySessionRepository repository;

    public ActiveSafetySession getSession(UUID userId) {

        return repository
                .findById(userId)
                .orElseThrow(() ->
                        new SessionNotFoundException(userId));

    }

}