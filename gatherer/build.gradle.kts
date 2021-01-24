plugins {
    application
}

val deps: Map<String, String> by extra

dependencies {
    implementation(project(":config"))
    // TODO these should be provided as an API dep of :config
    implementation("org.apache.commons", "commons-configuration2", "2.7")
    runtimeOnly("commons-beanutils", "commons-beanutils", "1.9.4")
    implementation(project(":model"))

    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "1.4.2")

    implementation("ch.qos.logback", "logback-classic", "1.2.3")

    implementation("com.github.kittinunf.fuel", "fuel", deps["fuel"])
    implementation("com.github.kittinunf.fuel", "fuel-coroutines", deps["fuel"])
    implementation("com.github.kittinunf.fuel", "fuel-jackson", deps["fuel"])

    implementation("com.google.guava", "guava", "30.1-jre")

    implementation("org.mpierce.guice.warmup", "guice-warmup", "0.1")
    implementation("com.google.inject", "guice", "4.2.3")

    implementation("com.fasterxml.jackson.core", "jackson-annotations", deps["jackson"])
    implementation("com.fasterxml.jackson.core", "jackson-core", deps["jackson"])
    implementation("com.fasterxml.jackson.core", "jackson-databind", deps["jackson"])
    implementation("com.fasterxml.jackson.datatype", "jackson-datatype-jsr310", deps["jackson"])
    implementation("com.fasterxml.jackson.module", "jackson-module-kotlin", deps["jackson"])

    implementation("org.influxdb", "influxdb-java", "2.21")

    testImplementation("com.github.tomakehurst", "wiremock", "2.27.2")
    testImplementation("io.mockk", "mockk", "1.10.5")
}

application {
    mainClass.set("net.dinomite.gatherer.DataGathererKt")
}

tasks {
    getByName<Zip>("distZip").enabled = false
}
