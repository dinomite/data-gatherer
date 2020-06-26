import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.3.72"
    application
}

repositories {
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.3")

    implementation("ch.qos.logback:logback-classic:1.2.3")

    implementation("org.apache.commons:commons-configuration2:2.6")
    implementation("commons-beanutils:commons-beanutils:1.9.4")

    implementation("com.github.kittinunf.fuel:fuel:2.2.1")
    implementation("com.github.kittinunf.fuel:fuel-coroutines:2.2.1")
    implementation("com.github.kittinunf.fuel:fuel-jackson:2.2.1")

    implementation("com.google.guava:guava:28.1-jre")

    implementation("org.mpierce.guice.warmup:guice-warmup:0.1")
    implementation("com.google.inject", "guice", "4.2.3")

    implementation("com.fasterxml.jackson.core:jackson-annotations:2.10.1")
    implementation("com.fasterxml.jackson.core:jackson-core:2.10.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.10.1")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.10.1")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.10.1")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.5.2")
    testImplementation(kotlin("test-junit5"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClassName = "net.dinomite.dg.DataGathererKt"
}
