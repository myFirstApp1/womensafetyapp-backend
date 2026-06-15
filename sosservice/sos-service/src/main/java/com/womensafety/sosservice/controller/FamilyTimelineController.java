package com.womensafety.sosservice.controller;

import com.womensafety.sosservice.dto.TimelineEventResponse;
import com.womensafety.sosservice.service.tracking.FamilyTimelineService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/family")
@RequiredArgsConstructor
public class FamilyTimelineController {

    private final FamilyTimelineService service;

    @GetMapping("/timeline")
    public List<TimelineEventResponse>
    getTimeline(
            @RequestParam String trackingId
    ) {

        return service.getTimeline(
                trackingId
        );
    }
}
