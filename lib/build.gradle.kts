plugins {
    kotlin("jvm")
    signing
    `maven-publish`
    id("org.jetbrains.dokka") version "1.5.31"
}

group = "xyz.davidsimon"
version = "1.0"
description = "An easy to use library to create interactive command line prompts"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")

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

val javadocJar = tasks.register("javadocJar", Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    archiveClassifier.set("javadoc")

    dependsOn(tasks.dokkaJavadoc)
    from(tasks.dokkaJavadoc)
}

publishing {
    publications.create(project.name, MavenPublication::class) {
        repositories {
            maven("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/") {
                credentials {
                    username = project.properties["ossrhUsername"].toString()
                    password = project.properties["ossrhPassword"].toString()
                }
            }
        }
        from(components["kotlin"])
        artifact(tasks.kotlinSourcesJar)
        artifact(javadocJar)

        pom {
            packaging = "jar"
            name.set("${project.group}:${project.name}")
            description.set(project.description)
            url.set("https://github.com/david-simon/interakt")

            developers {
                developer {
                    name.set("David Simon")
                    email.set("interakt@davidsimon.xyz")
                }
            }

            licenses {
                license {
                    name.set("MIT")
                    url.set("http://opensource.org/licenses/MIT")
                }
            }

            scm {
                connection.set("scm:git:ssh://github.com/david-simon/interakt.git")
                developerConnection.set("scm:git:ssh://git@github.com/david-simon/interakt.git")
                url.set("https://github.com/david-simon/interakt")
            }
        }
    }
}

signing {
    sign(publishing.publications[project.name])
}