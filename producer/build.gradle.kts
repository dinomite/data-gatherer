plugins {
    application
}

dependencies {
    implementation(project(":config"))
    implementation(project(":model"))

    implementation("io.ktor:ktor-server-core:1.4.1")
    implementation("io.ktor:ktor-server-netty:1.4.1")
    implementation("io.ktor:ktor-jackson:1.4.1")

    runtimeOnly("ch.qos.logback:logback-classic:1.2.9")

    implementation("org.apache.commons:commons-configuration2:2.7")
    implementation("commons-beanutils:commons-beanutils:1.9.4")

    implementation("com.fasterxml.jackson.core:jackson-core:2.12.5")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.12.5")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.12.5")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.5")
}

application {
    mainClass.set("net.dinomite.producer.Rtl433")
}
