@file:Suppress("UnstableApiUsage")

package net.dinomite.dg

import com.google.common.util.concurrent.Service
import kotlinx.coroutines.runBlocking
import net.dinomite.dg.services.TimeService
import org.apache.commons.configuration2.CompositeConfiguration
import org.apache.commons.configuration2.EnvironmentConfiguration
import org.apache.commons.configuration2.PropertiesConfiguration
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder
import org.apache.commons.configuration2.builder.fluent.Parameters
import java.nio.file.Files
import java.nio.file.Path

class DataGatherer(private val config: Settings) {
    private val services = listOf<Service>(
            TimeService(config)
    )

    fun start() = services.forEach { it.startAsync() }
    fun isRunning(): Boolean = services.firstOrNull { it.isRunning } != null
    fun stop() = services.forEach { it.stopAsync() }
}

const val USAGE = """
USAGE:
    DataGathererKt CONFIG_DIR
"""

fun main(args: Array<String>) {
    val config = buildConfiguration(args)

    val app = DataGatherer(config)
    Runtime.getRuntime().addShutdownHook(shutdownHook(app))

    app.start()
    while (app.isRunning()) {
        Thread.sleep(1000)
    }
}

private fun buildConfiguration(args: Array<String>): Settings {
    return CompositeConfiguration().apply {
        addConfiguration(EnvironmentConfiguration())
        val configPath = Path.of(args.getOrElse(0) { throw RuntimeException("CONFIG_DIR is required\n$USAGE") })
        Files.newDirectoryStream(configPath)
                .filter { it.toString().endsWith("properties") }
                .sortedDescending()
                .forEach { propertiesFile ->
                    this.addConfiguration(
                            FileBasedConfigurationBuilder(PropertiesConfiguration::class.java).apply {
                                configure(Parameters().properties().apply {
                                    setFileName(propertiesFile.toString())
                                })
                            }.configuration
                    )
                }
    }.let { Settings(it) }
}

private fun shutdownHook(dataGatherer: DataGatherer) = object : Thread() {
    override fun run() = runBlocking {
        println("Shutting down")
        dataGatherer.stop()
        println("Done")
    }
}
