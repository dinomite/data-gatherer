dependencies {
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.12.5")
    api("org.apache.commons:commons-collections4:4.4")

    testImplementation("com.fasterxml.jackson.core:jackson-core:2.12.5")
    testImplementation("com.fasterxml.jackson.core:jackson-databind:2.14.1")
    testImplementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.12.5")
    testImplementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.5")
}
