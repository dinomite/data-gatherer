plugins {
    application
}

val deps: Map<String, String> by extra

dependencies {
    implementation(project(":model"))

    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "1.3.3")

    implementation("ch.qos.logback", "logback-classic", "1.2.3")

    implementation("org.apache.commons", "commons-configuration2", "2.7")
    implementation("commons-beanutils", "commons-beanutils", "1.9.4")

    implementation("com.github.kittinunf.fuel", "fuel", "2.2.3")
    implementation("com.github.kittinunf.fuel", "fuel-coroutines", "2.2.3")
    implementation("com.github.kittinunf.fuel", "fuel-jackson", "2.2.3")

    implementation("com.google.guava", "guava", "29.0-jre")

    implementation("org.mpierce.guice.warmup", "guice-warmup", "0.1")
    implementation("com.google.inject", "guice", "4.2.3")

    implementation("com.fasterxml.jackson.core", "jackson-annotations", deps["jackson"])
    implementation("com.fasterxml.jackson.core", "jackson-core", deps["jackson"])
    implementation("com.fasterxml.jackson.core", "jackson-databind", deps["jackson"])
    implementation("com.fasterxml.jackson.datatype", "jackson-datatype-jsr310", deps["jackson"])
    implementation("com.fasterxml.jackson.module", "jackson-module-kotlin", deps["jackson"])
}

application {
    mainClassName = "net.dinomite.gatherer.DataGathererKt"
}
