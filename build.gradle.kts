plugins {
    application
    checkstyle
    jacoco
    id("org.springframework.boot") version "3.5.6"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("org.sonarqube") version "6.2.0.5505"
    id("io.freefair.lombok") version "9.0.0"
    id("io.sentry.jvm.gradle") version "5.12.2"
}

group = "hexlet.code"
version = "0.0.1-SNAPSHOT"
description = "Task manager application on Spring Boot"

val mapstructVersion by extra("1.6.3")

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

application {
    mainClass = "hexlet.code.demo.AppApplication"
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-test")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.mapstruct:mapstruct:$mapstructVersion")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.openapitools:jackson-databind-nullable:0.2.7")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.13")
    implementation("io.sentry:sentry-opentelemetry-agent:8.31.0")

    runtimeOnly("com.h2database:h2")
    runtimeOnly("org.postgresql:postgresql")

    annotationProcessor("org.mapstruct:mapstruct-processor:$mapstructVersion")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.instancio:instancio-junit:5.5.1")
    testImplementation("net.javacrumbs.json-unit:json-unit-assertj:4.1.1")
    testImplementation ("io.zonky.test:embedded-database-spring-test:2.6.0")
    testImplementation("net.datafaker:datafaker:2.5.2")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

checkstyle {
    toolVersion = "10.3.4"
    configFile = rootProject.file("config/checkstyle/checkstyle.xml")
    isIgnoreFailures = false
}

sonar {
    properties {
        property("sonar.projectKey", "Nikita14_java-project-99")
        property("sonar.organization", "nikita14")
        property("sonar.host.url", "https://sonarcloud.io")
    }
}

sentry {
    includeSourceContext = true

    org = "nikita-ny"
    projectName = "java-spring-boot"
    authToken = System.getenv("SENTRY_AUTH_TOKEN")
}

tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}
//
tasks.register("sentryBundleSourcesJava") {
    enabled = System.getenv("SENTRY_AUTH_TOKEN") != null
}