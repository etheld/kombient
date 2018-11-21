import org.gradle.api.JavaVersion.VERSION_1_8
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.3.10"
    application
    idea
    kotlin("plugin.jpa") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    kotlin("jvm") version kotlinVersion
    id("org.springframework.boot") version "2.1.0.RELEASE"
    id("io.gitlab.arturbosch.detekt") version "1.0.0.RC8"
    id("com.gorylenko.gradle-git-properties") version "1.5.1"
    id("io.spring.dependency-management") version "1.0.6.RELEASE"
}

// spring dependency manager
dependencyManagement {
    val cloudVersion: String by project
    imports {
        mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:$cloudVersion")
    }
}

repositories {
    mavenCentral()
    maven("https://repo.spring.io/snapshot")
    maven("https://repo.spring.io/milestone")
}

dependencies {
    val spekVersion: String by project
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:+")

    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("io.micrometer:micrometer-core")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
    implementation("io.github.openfeign:feign-gson:+")

    implementation("org.zalando:logbook-spring-boot-starter:1.8.1")

    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.springframework.boot:spring-boot-starter-data-rest")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.retry:spring-retry")
    implementation("org.springframework.cloud:spring-cloud-starter-vault-config")
    implementation("org.springframework.boot:spring-boot-devtools")

    implementation("org.jsoup:jsoup:1.+")
    implementation("com.google.guava:guava:19.+")

    implementation("me.xdrop:fuzzywuzzy:1.+")

    testImplementation("org.mockito:mockito-all:2.+")
    testImplementation("com.nhaarman:mockito-kotlin:1.+")

    testImplementation("org.assertj:assertj-core:3.+")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
    testImplementation("org.springframework.cloud:spring-cloud-contract-wiremock")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "junit")
    }

    testImplementation("org.jetbrains.spek:spek-api:$spekVersion")
    testRuntime("org.jetbrains.spek:spek-junit-platform-engine:$spekVersion")
    testRuntime("org.junit.jupiter:junit-jupiter-engine")
    testRuntime("org.junit.platform:junit-platform-engine")
    testRuntime("com.h2database:h2")


    runtime("org.postgresql:postgresql")

}

application {
    mainClassName = "kombient.ApplicationKt"
}

springBoot {
    buildInfo {
        version = file("version").readText()
    }
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            languageVersion = "1.3"
            apiVersion = "1.3"
            javaParameters = true
            jvmTarget = VERSION_1_8.toString()
        }
    }
    withType<Test> {
        useJUnitPlatform()
    }
}

