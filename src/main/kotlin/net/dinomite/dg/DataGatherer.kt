@file:Suppress("UnstableApiUsage")

package net.dinomite.dg

import com.google.common.util.concurrent.Service
import com.google.common.util.concurrent.ServiceManager
import kotlinx.coroutines.runBlocking
import net.dinomite.dg.services.TimeService
import org.apache.commons.configuration2.CompositeConfiguration
import org.apache.commons.configuration2.EnvironmentConfiguration
import org.apache.commons.configuration2.PropertiesConfiguration
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder
import org.apache.commons.configuration2.builder.fluent.Parameters
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Path
import java.time.Duration
import java.util.concurrent.TimeoutException

const val USAGE = """
USAGE:
    DataGathererKt CONFIG_DIR
"""
val STOP_DURATION: Duration = Duration.ofSeconds(5)

val logger: Logger = LoggerFactory.getLogger("DataGatherer app")

fun main(args: Array<String>) {
    val config = buildConfiguration(args)

    val services = listOf<Service>(
            TimeService(config)
    )

    val serviceManager = ServiceManager(services)
    serviceManager.addListener(object : ServiceManager.Listener() {
        override fun failure(service: Service) {
            logger.warn("Failed to stop ${service.javaClass.simpleName}")
            super.failure(service)
        }
    })
    Runtime.getRuntime().addShutdownHook(shutdownHook(serviceManager))

    serviceManager.startAsync()
    serviceManager.awaitStopped()
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

private fun shutdownHook(serviceManager: ServiceManager) = object : Thread() {
    override fun run() = runBlocking {
        logger.info("Shutting down")
        try {
            serviceManager.stopAsync().awaitStopped(STOP_DURATION)
        } catch (e: TimeoutException) {
            logger.warn("Timed out while stopping services", e)
        }
        logger.info("Done")
    }
}
