# Use stable OpenJDK base image
FROM eclipse-temurin:17-jdk-jammy

# Set working directory
WORKDIR /app

# Copy the JAR file
COPY build/libs/*.jar app.jar

# Expose service port
EXPOSE 8084

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]