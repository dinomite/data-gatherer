val deps: Map<String, String> by extra

dependencies {
    implementation("com.fasterxml.jackson.core", "jackson-annotations", deps["jackson"])

    testImplementation("com.fasterxml.jackson.core", "jackson-core", deps["jackson"])
    testImplementation("com.fasterxml.jackson.core", "jackson-databind", deps["jackson"])
    testImplementation("com.fasterxml.jackson.datatype", "jackson-datatype-jsr310", deps["jackson"])
    testImplementation("com.fasterxml.jackson.module", "jackson-module-kotlin", deps["jackson"])

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.2")
    testImplementation(kotlin("test-junit5"))
}