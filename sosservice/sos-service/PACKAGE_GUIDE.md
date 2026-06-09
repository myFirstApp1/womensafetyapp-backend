# Service Package Structure - Complete Guide

## ✅ Already Moved (7 files)
```
service/
├── core/
│   ├── SessionManager.java ✓
│   └── TimelineMapper.java ✓
└── sos/
    ├── ISosTriggerService.java ✓
    ├── SosTriggerService.java ✓
    ├── SosEventService.java ✓
    ├── SosOutboxProcessorService.java ✓
    └── SosRetryService.java ✓
```

## 📋 Remaining Files (37 files) - Where They Go

### Communication (7 files)
```
service/communication/
├── ICommunicationDecisionService.java
├── CommunicationDecisionService.java
├── CommunicationFallbackService.java
├── EmergencyCommunicationService.java
├── NotificationService.java (interface)
├── NotificationServiceImpl.java (implementation)
└── KafkaProducerService.java
```

### Heartbeat (4 files)
```
service/heartbeat/
├── IHeartbeatCheckService.java
├── HeartbeatCheckService.java
├── HeartbeatLossService.java
└── WearableHeartbeatService.java
```

### Location (4 files)
```
service/location/
├── IGpsIntelligenceService.java
├── GpsIntelligenceService.java
├── LocationIntelligenceService.java
└── LocationRecoveryService.java
```

### Risk (4 files)
```
service/risk/
├── IRiskDecisionEngine.java
├── RiskDecisionEngine.java
├── RiskScoreCalculatorService.java
└── TamperDetectionService.java
```

### Incident (4 files)
```
service/incident/
├── IIncidentResponseService.java
├── IncidentResponseService.java
├── PoliceIncidentPacketService.java
└── PoliceIntegrationService.java
```

### Tracking (4 files)
```
service/tracking/
├── FamilyTrackingService.java
├── FamilyDashboardService.java
├── FamilyTimelineService.java
└── TrackingService.java
```

### Timeline (4 files)
```
service/timeline/
├── EmergencyTimelineService.java
├── EvidenceService.java
├── EvidenceRetrievalService.java
└── EmergencyOrchestratorService.java
```

### Sensor (4 files)
```
service/sensor/
├── SensorFusionService.java
├── SensorFusionOrchestratorService.java
├── OffBodyIntelligenceService.java
└── AiSensorRulesService.java
```

### PreAlert (1 file)
```
service/prealert/
└── PreAlertService.java
```

### Acknowledgement (2 files)
```
service/acknowledgement/
├── AcknowledgementService.java
└── DeliveryConfirmationService.java
```

---

## Key Imports After Migration

- Files in `service/sos/` → import from `service.communication`, `service.timeline`, `service.core`
- Files in `service/heartbeat/` → import from `service.risk`, `service.sensor`, `service.sos`, `service.core`
- Files in `service/location/` → import from `service.risk`, `service.incident`
- Files in `service/tracking/` → import from `service.core` (TimelineMapper)
- All files → import from `service.core.SessionManager`
- All files → import from `service.timeline.EmergencyTimelineService`

