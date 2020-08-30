import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.72"
    java
    id("com.github.ben-manes.versions") version "0.29.0"
    id("dev.jacomet.logging-capabilities") version "0.9.0"
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

    repositories {
        jcenter()
    }

    val deps by extra {
        mapOf(
                "jackson" to "2.11.2",
                "jupiter" to "5.6.2"
        )
    }

    dependencies {
        implementation(kotlin("stdlib-jdk8"))

        testImplementation("org.junit.jupiter", "junit-jupiter-api", deps["jupiter"])
        testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine", deps["jupiter"])
        testImplementation(kotlin("test-junit5"))
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
    }

    tasks.test {
        useJUnitPlatform()
    }
}
