plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow") version "7.1.0"
}

group = "xyz.davidsimon"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":interakt"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
}

tasks.compileKotlin {
    kotlinOptions {
        jvmTarget = "11"
    }
}

tasks.shadowJar {
    manifest {
        attributes(mapOf(
            "Main-Class" to "xyz.davidsimon.interakt.demo.MainKt"
        ))
    }
}