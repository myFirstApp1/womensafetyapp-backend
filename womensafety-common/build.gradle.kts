plugins {
	`java-library`
	`maven-publish`
}

group = "com.tl.womensafety"
version = "0.1.0"

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(17))
	}
	withSourcesJar()
	withJavadocJar()
}

tasks.withType<JavaCompile>().configureEach {
	options.encoding = "UTF-8"
	options.release.set(17)
}

// ✅ MUST come BEFORE dependencies
repositories {
	mavenCentral()
	mavenLocal()
}

dependencies {
	api("org.springframework.kafka:spring-kafka:3.3.0")
	api("org.projectlombok:lombok:1.18.32")

	compileOnly("org.projectlombok:lombok:1.18.32")
	annotationProcessor("org.projectlombok:lombok:1.18.32")
}

publishing {
	publications {
		create<MavenPublication>("mavenJava") {
			from(components["java"])
			pom {
				name.set("womensafety-common")
				description.set("Shared DTOs and domain events for Women Safety microservices")
			}
		}
	}
}