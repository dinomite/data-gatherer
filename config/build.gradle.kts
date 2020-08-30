val deps: Map<String, String> by extra

dependencies {
    api("org.apache.commons", "commons-configuration2", "2.7")
    runtimeOnly("commons-beanutils", "commons-beanutils", "1.9.4")
}
