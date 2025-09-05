# Use lightweight JDK image
FROM openjdk:17-jdk-slim

# Create app dir inside container
VOLUME /tmp

# Copy jar from Gradle build
COPY build/libs/*.jar app.jar

# Run the app
ENTRYPOINT ["java", "-jar", "/app.jar"]