# Use stable OpenJDK base image
FROM eclipse-temurin:17-jdk-jammy

# Set working directory
WORKDIR /app

# Copy JAR file from Gradle build output
COPY build/libs/*.jar app.jar

# Expose service port (change this accordingly)
# Auth: 8081 | User: 8082 | Notification: 8083 | SOS: 8084
EXPOSE 8081

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
