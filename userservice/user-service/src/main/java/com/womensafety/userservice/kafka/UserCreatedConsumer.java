package com.womensafety.userservice.kafka;

import com.womensafety.userservice.event.UserCreatedEvent;
import com.womensafety.userservice.model.UserProfile;
import com.womensafety.userservice.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserCreatedConsumer {

    private final UserProfileRepository userProfileRepository;

    @KafkaListener(
            topics = "${app.verification.topic}",
            groupId = "user-service",
            containerFactory = "userCreatedEventKafkaListenerContainerFactory"
    )
    public void consume(UserCreatedEvent event) {
        log.info("Received user.created event for userId: {}", event.getUserId());

        // Check if profile already exists
        userProfileRepository.findByUserId(event.getUserId())
                .ifPresentOrElse(
                        existing -> log.info("Profile already exists for userId {}", event.getUserId()),
                        () -> {
                            UserProfile profile = UserProfile.builder()
                                    .userId(event.getUserId())
                                    .name(event.getUsername()) // optional: pre-fill username as name
                                    .isVerified(event.isVerified()) // can store if you added this field
                                    .build();
                            userProfileRepository.save(profile);
                            log.info("Created new profile for userId {}", event.getUserId());
                        }
                );
    }
}