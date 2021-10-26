plugins {
    kotlin("jvm")
}

group = "xyz.davidsimon"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")

    api("org.jline:jline-terminal-jansi:3.21.0")
    api("org.jline:jline-reader:3.21.0")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.compileKotlin {
    kotlinOptions {
        jvmTarget = "16"
    }
}

tasks.compileTestKotlin {
    kotlinOptions {
        jvmTarget = "16"
    }
}

tasks.jar {
    archiveBaseName.set("interakt")
}