FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

# Copy JAR with explicit name (no wildcard)
COPY build/libs/userservice-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8082
#Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]


