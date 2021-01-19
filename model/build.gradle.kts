val deps: Map<String, String> by extra

dependencies {
    implementation("com.fasterxml.jackson.core", "jackson-annotations", deps["jackson"])
    api("org.apache.commons", "commons-collections4", "4.4")

    testImplementation("com.fasterxml.jackson.core", "jackson-core", deps["jackson"])
    testImplementation("com.fasterxml.jackson.core", "jackson-databind", deps["jackson"])
    testImplementation("com.fasterxml.jackson.datatype", "jackson-datatype-jsr310", deps["jackson"])
    testImplementation("com.fasterxml.jackson.module", "jackson-module-kotlin", deps["jackson"])
}
