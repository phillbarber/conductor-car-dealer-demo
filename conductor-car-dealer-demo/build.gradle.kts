import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.21"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

    implementation("com.netflix.conductor:conductor-client:3.13.7")
    implementation("com.netflix.conductor:conductor-common:3.13.7")

    testImplementation("com.netflix.conductor:conductor-server:3.13.7")
    testImplementation(kotlin("test"))
}



tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("MainKt")
}