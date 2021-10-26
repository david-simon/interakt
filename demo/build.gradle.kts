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
    implementation(project(":lib"))
}

tasks.compileKotlin {
    kotlinOptions {
        jvmTarget = "16"
    }
}

tasks.shadowJar {
    archiveBaseName.set("interakt-demo")

    manifest {
        attributes(mapOf(
            "Main-Class" to "xyz.davidsimon.interakt.demo.MainKt"
        ))
    }
}