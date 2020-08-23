plugins {
    application
}

val deps: Map<String, String> by extra

dependencies {
    implementation(project(":model"))

    implementation("io.ktor", "ktor-server-core", "1.4.0")
    implementation("io.ktor", "ktor-server-netty", "1.4.0")
    implementation("io.ktor", "ktor-jackson", "1.4.0")

    runtimeOnly("ch.qos.logback", "logback-classic", "1.2.3")

    implementation("org.apache.commons", "commons-configuration2", "2.7")
    implementation("commons-beanutils", "commons-beanutils", "1.9.4")

    implementation("com.fasterxml.jackson.core", "jackson-core", deps["jackson"])
    implementation("com.fasterxml.jackson.core", "jackson-databind", deps["jackson"])
    implementation("com.fasterxml.jackson.datatype", "jackson-datatype-jsr310", deps["jackson"])
    implementation("com.fasterxml.jackson.module", "jackson-module-kotlin", deps["jackson"])
}

application {
    mainClassName = "data.producer.rtl_433.AppKt"
}
