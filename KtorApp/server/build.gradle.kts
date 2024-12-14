plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
    id("io.ktor.plugin") version "3.0.2" // Ktor plugin for 3.x
    kotlin("plugin.serialization") version "2.1.0"
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
}

dependencies {
    // Ktor dependencies for version 3.0.2
    implementation("io.ktor:ktor-server-core:3.0.2")   // Core server dependencies
    implementation("io.ktor:ktor-server-netty:3.0.2")  // Netty engine for running the server
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.0.2")
    implementation("io.ktor:ktor-server-content-negotiation:3.0.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    implementation("ch.qos.logback:logback-classic:1.4.14")  // Logging

    // Testing dependencies
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.9.0")  // Kotlin JUnit testing support
}
