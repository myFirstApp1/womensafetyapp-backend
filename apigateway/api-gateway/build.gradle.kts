plugins {
    java
    id("org.springframework.boot") version "3.5.4"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.womensafety"
version = "0.0.1-SNAPSHOT"
description = "API Gateway for Women Safety Application"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenLocal()
    mavenCentral()
}

ext {
    set("springCloudVersion", "2025.0.3")
}

dependencies {

    // API Gateway
    implementation("org.springframework.cloud:spring-cloud-starter-gateway-server-webflux")

    // Security
    implementation("org.springframework.boot:spring-boot-starter-security")

    // JWT
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

    // Actuator
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
}

dependencyManagement {
    imports {
        mavenBom(
            "org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}"
        )
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}