# Service Package Reorganization Guide - VS Code Instructions

## Overview
Reorganize 44 service classes from `service/` into 12 domain-specific sub-packages for better code organization and maintainability.

## Migration Steps (Using VS Code Refactoring)

### Phase 1: Core Utilities (Already Completed ✓)
- `SessionManager.java` → `service/core/` ✓
- `TimelineMapper.java` → `service/core/` ✓

---

### Phase 2: SOS Management (5 files)
**Target Package:** `com.womensafety.sosservice.service.sos`

**Files to move:**
1. ISosTriggerService.java
2. SosTriggerService.java
3. SosEventService.java
4. SosOutboxProcessorService.java
5. SosRetryService.java

**VS Code Instructions:**
```
For each file:
1. Right-click file in Explorer
2. Select "Move..." (or Refactor → Move)
3. Type new path: src/main/java/com/womensafety/sosservice/service/sos/
4. VS Code automatically updates all imports
```

**Key Dependencies (will be auto-updated):**
- CommunicationDecisionService → import from service.communication
- EmergencyTimelineService → import from service.timeline
- SessionManager → import from service.core

---

### Phase 3: Communication (7 files)
**Target Package:** `com.womensafety.sosservice.service.communication`

**Files to move:**
1. ICommunicationDecisionService.java
2. CommunicationDecisionService.java
3. CommunicationFallbackService.java
4. EmergencyCommunicationService.java
5. NotificationService.java
6. NotificationServiceImpl.java
7. KafkaProducerService.java

---

### Phase 4: Heartbeat Monitoring (4 files)
**Target Package:** `com.womensafety.sosservice.service.heartbeat`

**Files to move:**
1. IHeartbeatCheckService.java
2. HeartbeatCheckService.java
3. HeartbeatLossService.java
4. WearableHeartbeatService.java

**Key Dependencies:**
- RiskScoreCalculatorService → service.risk
- OffBodyIntelligenceService → service.sensor
- SosTriggerService → service.sos
- SessionManager → service.core

---

### Phase 5: Location Intelligence (4 files)
**Target Package:** `com.womensafety.sosservice.service.location`

**Files to move:**
1. IGpsIntelligenceService.java
2. GpsIntelligenceService.java
3. LocationIntelligenceService.java
4. LocationRecoveryService.java

---

### Phase 6: Risk Assessment (4 files)
**Target Package:** `com.womensafety.sosservice.service.risk`

**Files to move:**
1. IRiskDecisionEngine.java
2. RiskDecisionEngine.java
3. RiskScoreCalculatorService.java
4. TamperDetectionService.java

---

### Phase 7: Incident Management (4 files)
**Target Package:** `com.womensafety.sosservice.service.incident`

**Files to move:**
1. IIncidentResponseService.java
2. IncidentResponseService.java
3. PoliceIncidentPacketService.java
4. PoliceIntegrationService.java

---

### Phase 8: Family Tracking (4 files)
**Target Package:** `com.womensafety.sosservice.service.tracking`

**Files to move:**
1. FamilyTrackingService.java
2. FamilyDashboardService.java
3. FamilyTimelineService.java
4. TrackingService.java

---

### Phase 9: Emergency Timeline & Evidence (4 files)
**Target Package:** `com.womensafety.sosservice.service.timeline`

**Files to move:**
1. EmergencyTimelineService.java
2. EvidenceService.java
3. EvidenceRetrievalService.java
4. EmergencyOrchestratorService.java

---

### Phase 10: Sensor Fusion (4 files)
**Target Package:** `com.womensafety.sosservice.service.sensor`

**Files to move:**
1. SensorFusionService.java
2. SensorFusionOrchestratorService.java
3. OffBodyIntelligenceService.java
4. AiSensorRulesService.java

---

### Phase 11: Pre-Alert Management (1 file)
**Target Package:** `com.womensafety.sosservice.service.prealert`

**Files to move:**
1. PreAlertService.java

---

### Phase 12: Acknowledgement & Delivery (2 files)
**Target Package:** `com.womensafety.sosservice.service.acknowledgement`

**Files to move:**
1. AcknowledgementService.java
2. DeliveryConfirmationService.java

---

## Verification Checklist

After completing all phases:

- [ ] All 44 files moved to correct packages
- [ ] No compilation errors (build test)
- [ ] All imports auto-updated correctly
- [ ] Package declarations updated
- [ ] Original files removed from `service/` root
- [ ] Integration tests passing

## Commands to Verify

```bash
# Build project
./gradlew.bat build --no-daemon

# Run tests
./gradlew.bat test

# Check for import errors
# (Check Problems panel in VS Code)
```

---

## Expected Result

```
src/main/java/com/womensafety/sosservice/service/
├── sos/                    (5 files)
├── communication/          (7 files)
├── heartbeat/             (4 files)
├── location/              (4 files)
├── risk/                  (4 files)
├── incident/              (4 files)
├── tracking/              (4 files)
├── timeline/              (4 files)
├── sensor/                (4 files)
├── prealert/              (1 file)
├── acknowledgement/       (2 files)
└── core/                  (2 files) ✓
```

All files properly organized with correct package hierarchy!
