plugins {
    kotlin("jvm") version "1.5.31"
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
}

allprojects {
    group = "xyz.davidsimon"
    version = "0.3.0"
    description = "An easy to use library to create interactive command line prompts"
}

repositories {
    mavenCentral()
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
        }
    }
}