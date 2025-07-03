import org.gradle.kotlin.dsl.implementation

plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

group = "com.ayun"
version = "0.0.1-SNAPSHOT"

allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {
    extra["springCloudVersion"] = "2025.0.0"

    apply(plugin = "kotlin")

    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "org.jetbrains.kotlin.plugin.jpa")

    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")

    dependencies {
        implementation("org.springframework.boot:spring-boot-starter-actuator")
        implementation("org.jetbrains.kotlin:kotlin-reflect")

        // Virtual Thread
        implementation("org.springframework.boot:spring-boot-starter-tomcat")

        implementation("org.springframework.kafka:spring-kafka")
        developmentOnly("org.springframework.boot:spring-boot-devtools")

        implementation("com.github.loki4j:loki-logback-appender:1.4.2")

        // Micrometer OTLP 의존성 추가
        implementation("io.micrometer:micrometer-registry-otlp")
        implementation("io.micrometer:micrometer-core")
        implementation("io.micrometer:micrometer-observation")
        implementation("io.micrometer:micrometer-registry-prometheus")

        implementation("io.opentelemetry:opentelemetry-api")
        implementation("io.opentelemetry:opentelemetry-context")

//    developmentOnly("org.springframework.boot:spring-boot-docker-compose")
//        runtimeOnly("io.micrometer:micrometer-registry-otlp")

        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
        testImplementation("org.springframework.kafka:spring-kafka-test")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    }

    dependencyManagement {
        imports {
            mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
        }
    }
}
