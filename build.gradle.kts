import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.21"
    java
    id("com.github.ben-manes.versions") version "0.29.0"
    id("dev.jacomet.logging-capabilities") version "0.9.0"
    jacoco
}

loggingCapabilities {
    enforceLogback()
}

repositories {
    jcenter()
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "jacoco")

    repositories {
        jcenter()
    }

    val deps by extra {
        mapOf(
                "fuel" to "2.2.3",
                "jackson" to "2.11.2",
                "jupiter" to "5.6.2"
        )
    }

    dependencies {
        implementation(kotlin("stdlib-jdk8"))
        implementation(kotlin("reflect"))

        testImplementation("org.junit.jupiter", "junit-jupiter-api", deps["jupiter"])
        testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine", deps["jupiter"])
        testImplementation(kotlin("test-junit5"))
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "13"
    }

    tasks.test {
        useJUnitPlatform()
    }

    tasks.jacocoTestReport {
        dependsOn(tasks.test)
        reports {
            xml.isEnabled = true
            html.isEnabled = false
        }
    }
}
