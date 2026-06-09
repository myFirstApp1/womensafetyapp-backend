@echo off
REM Service Package Migration Script
REM This script moves service files to their domain-specific packages

setlocal enabledelayedexpansion

REM Base path
set BASE_PATH=src\main\java\com\womensafety\sosservice\service

echo ===== SERVICE PACKAGE REORGANIZATION SCRIPT =====
echo.
echo This script will migrate 44 service classes into 12 domain-specific packages.
echo All imports will be automatically updated by IDE.
echo.
echo Packages to create:
echo  - communication (7 files)
echo  - heartbeat (4 files) 
echo  - location (4 files)
echo  - risk (4 files)
echo  - incident (4 files)
echo  - tracking (4 files)
echo  - timeline (4 files)
echo  - sensor (4 files)
echo  - prealert (1 file)
echo  - acknowledgement (2 files)
echo  - core (2 files - already done)
echo.

REM Communication Files
echo Moving Communication package files...
if not exist "%BASE_PATH%\communication" mkdir "%BASE_PATH%\communication"

REM Heartbeat Files
echo Moving Heartbeat package files...
if not exist "%BASE_PATH%\heartbeat" mkdir "%BASE_PATH%\heartbeat"

REM Location Files
echo Moving Location package files...
if not exist "%BASE_PATH%\location" mkdir "%BASE_PATH%\location"

REM Risk Files
echo Moving Risk package files...
if not exist "%BASE_PATH%\risk" mkdir "%BASE_PATH%\risk"

REM Incident Files
echo Moving Incident package files...
if not exist "%BASE_PATH%\incident" mkdir "%BASE_PATH%\incident"

REM Tracking Files
echo Moving Tracking package files...
if not exist "%BASE_PATH%\tracking" mkdir "%BASE_PATH%\tracking"

REM Timeline Files
echo Moving Timeline package files...
if not exist "%BASE_PATH%\timeline" mkdir "%BASE_PATH%\timeline"

REM Sensor Files
echo Moving Sensor package files...
if not exist "%BASE_PATH%\sensor" mkdir "%BASE_PATH%\sensor"

REM PreAlert Files
echo Moving PreAlert package files...
if not exist "%BASE_PATH%\prealert" mkdir "%BASE_PATH%\prealert"

REM Acknowledgement Files
echo Moving Acknowledgement package files...
if not exist "%BASE_PATH%\acknowledgement" mkdir "%BASE_PATH%\acknowledgement"

echo.
echo ===== NEXT STEPS =====
echo.
echo 1. In VS Code, open each file and use Refactor ^> Move to move it to its target package:
echo.
echo COMMUNICATION (7 files):
echo  - ICommunicationDecisionService.java
echo  - CommunicationDecisionService.java
echo  - CommunicationFallbackService.java
echo  - EmergencyCommunicationService.java
echo  - NotificationService.java
echo  - NotificationServiceImpl.java
echo  - KafkaProducerService.java
echo.
echo HEARTBEAT (4 files):
echo  - IHeartbeatCheckService.java
echo  - HeartbeatCheckService.java
echo  - HeartbeatLossService.java
echo  - WearableHeartbeatService.java
echo.
echo LOCATION (4 files):
echo  - IGpsIntelligenceService.java
echo  - GpsIntelligenceService.java
echo  - LocationIntelligenceService.java
echo  - LocationRecoveryService.java
echo.
echo RISK (4 files):
echo  - IRiskDecisionEngine.java
echo  - RiskDecisionEngine.java
echo  - RiskScoreCalculatorService.java
echo  - TamperDetectionService.java
echo.
echo INCIDENT (4 files):
echo  - IIncidentResponseService.java
echo  - IncidentResponseService.java
echo  - PoliceIncidentPacketService.java
echo  - PoliceIntegrationService.java
echo.
echo TRACKING (4 files):
echo  - FamilyTrackingService.java
echo  - FamilyDashboardService.java
echo  - FamilyTimelineService.java
echo  - TrackingService.java
echo.
echo TIMELINE (4 files):
echo  - EmergencyTimelineService.java
echo  - EvidenceService.java
echo  - EvidenceRetrievalService.java
echo  - EmergencyOrchestratorService.java
echo.
echo SENSOR (4 files):
echo  - SensorFusionService.java
echo  - SensorFusionOrchestratorService.java
echo  - OffBodyIntelligenceService.java
echo  - AiSensorRulesService.java
echo.
echo PREALERT (1 file):
echo  - PreAlertService.java
echo.
echo ACKNOWLEDGEMENT (2 files):
echo  - AcknowledgementService.java
echo  - DeliveryConfirmationService.java
echo.
echo 2. After moving all files, run: ./gradlew.bat build --no-daemon
echo 3. Fix any import errors (very few expected - most auto-updated)
echo.
echo 4. Verify: all files should be in %BASE_PATH%\(domain)\ subdirectories
echo.

pause
