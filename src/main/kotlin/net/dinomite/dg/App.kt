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
import java.time.Duration

class App {
    private val services = mutableListOf<Service>()

    fun start() {
        services.add(TimeService(Duration.ofSeconds(2)))

        services.forEach {
            it.startAsync()
        }
    }

    fun isRunning(): Boolean {
        val first = services.firstOrNull {
            it.isRunning
        }

        return first != null
    }

    fun stop() {
        services.forEach {
            it.stopAsync()
        }
    }
}

const val USAGE = """
USAGE:
    AppKt CONFIG_DIR
"""

fun main(args: Array<String>) {
    val config = CompositeConfiguration().apply {
        addConfiguration(EnvironmentConfiguration())
        addConfigFiles(args.getOrElse(0) { throw RuntimeException("CONFIG_DIR is required\n$USAGE") }, this)
    }

    val app = App()
    Runtime.getRuntime().addShutdownHook(shutdownHook(app))

    app.start()
    while (app.isRunning()) {
        Thread.sleep(1000)
    }
}

fun addConfigFiles(configDir: String, compositeConfiguration: CompositeConfiguration) {
    Files.newDirectoryStream(Path.of(configDir))
            .filter { it.toString().endsWith("properties") }
            .sortedDescending()
            .forEach { propertiesFile ->
                compositeConfiguration.addConfiguration(
                        FileBasedConfigurationBuilder(PropertiesConfiguration::class.java).apply {
                            configure(Parameters().properties().apply {
                                setFileName(propertiesFile.toString())
                            })
                        }.configuration
                )
            }
}

private fun shutdownHook(app: App) = object : Thread() {
    override fun run() = runBlocking {
        println("Shutting down")
        app.stop()
        println("Done")
    }
}
