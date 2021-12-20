plugins {
    application
}

dependencies {
    implementation(project(":config"))
    // TODO these should be provided as an API dep of :config
    implementation("org.apache.commons:commons-configuration2:2.7")
    runtimeOnly("commons-beanutils:commons-beanutils:1.9.4")
    implementation(project(":model"))

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")

    implementation("ch.qos.logback:logback-classic:1.2.3")

    implementation("com.github.kittinunf.fuel:fuel:2.3.1")
    implementation("com.github.kittinunf.fuel:fuel-coroutines:2.3.1")
    implementation("com.github.kittinunf.fuel:fuel-jackson:2.3.1")

    implementation("com.google.guava:guava:30.1-jre")

    implementation("org.mpierce.guice.warmup:guice-warmup:0.2")
    implementation("com.google.inject:guice:4.2.3")

    implementation("com.fasterxml.jackson.core:jackson-annotations:2.12.5")
    implementation("com.fasterxml.jackson.core:jackson-core:2.12.5")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.1")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.12.5")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.5")

    implementation("org.influxdb:influxdb-java:2.21")

    testImplementation("com.github.tomakehurst:wiremock:2.27.2")
    testImplementation("io.mockk:mockk:1.10.5")
    testImplementation("io.apisense.embed.influx:embed-influxDB:1.2.1") {
        exclude("org.slf4j", "slf4j-log4j12")
    }
    testImplementation("org.slf4j:log4j-over-slf4j:1.7.30")
}

application {
    mainClass.set("net.dinomite.gatherer.DataGathererKt")
}

tasks {
    getByName<Zip>("distZip").enabled = false
}

tasks.test {
    if (project.hasProperty("excludeTests")) {
        exclude(project.property("excludeTests").toString())
    }
}
