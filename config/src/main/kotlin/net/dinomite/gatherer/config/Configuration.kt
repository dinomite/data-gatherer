package net.dinomite.gatherer.config

import org.apache.commons.configuration2.CompositeConfiguration
import org.apache.commons.configuration2.PropertiesConfiguration
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder
import org.apache.commons.configuration2.builder.fluent.Parameters
import java.nio.file.Files
import java.nio.file.Path

fun <T> buildConfiguration(
    args: Array<String>,
    usage: String,
    configBuilder: (CompositeConfiguration) -> T
): T {
    val configPath = Path.of(args.getOrNull(0) ?: throw RuntimeException("CONFIG_DIR is required\n$usage"))
    if (!configPath.toFile().isDirectory) {
        throw RuntimeException("${configPath.toAbsolutePath()} isn't a directory")
    }

    return CompositeConfiguration().apply {
        Files.newDirectoryStream(configPath)
            .filter { it.toString().endsWith("properties") }
            .sortedDescending()
            .forEach { propertiesFile ->
                addConfiguration(
                    FileBasedConfigurationBuilder(PropertiesConfiguration::class.java).apply {
                        configure(Parameters().properties().apply {
                            setFileName(propertiesFile.toString())
                        })
                    }.configuration
                )
            }
    }.let { configBuilder.invoke(it) }
}
