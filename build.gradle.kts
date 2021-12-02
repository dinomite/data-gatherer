import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.0"
    java
    id("com.github.ben-manes.versions") version "0.39.0"
    id("dev.jacomet.logging-capabilities") version "0.9.0"
    jacoco
}

loggingCapabilities {
    enforceLogback()
}

repositories {
    mavenCentral()
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "jacoco")

    repositories {
        mavenCentral()
    }

    dependencies {
        implementation(kotlin("stdlib-jdk8"))
        implementation(kotlin("reflect"))

        testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
        testImplementation(kotlin("test-junit5"))
    }

    tasks {
        withType<KotlinCompile> {
            kotlinOptions.jvmTarget = "13"
        }

        test {
            useJUnitPlatform()
        }

        jacocoTestReport {
            dependsOn(test)
            reports {
                xml.isEnabled = true
                html.isEnabled = false
            }
        }
    }

}
