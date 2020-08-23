plugins {
    application
}

val deps: Map<String, String> by extra

dependencies {
    implementation("io.ktor", "ktor-server-core", "1.4.0")
    implementation("io.ktor", "ktor-server-netty", "1.4.0")
    implementation("io.ktor", "ktor-jackson", "1.4.0")

    runtimeOnly("ch.qos.logback", "logback-classic", "1.2.3")

    implementation("com.fasterxml.jackson.core", "jackson-core", "2.11.2")
    implementation("com.fasterxml.jackson.core", "jackson-databind", "2.11.2")
    implementation("com.fasterxml.jackson.datatype", "jackson-datatype-jsr310", "2.11.2")
    implementation("com.fasterxml.jackson.module", "jackson-module-kotlin", "2.11.2")
}

application {
    mainClassName = "data.producer.rtl_433.AppKt"
}
