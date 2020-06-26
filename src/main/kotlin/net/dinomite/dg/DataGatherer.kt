@file:Suppress("UnstableApiUsage")

package net.dinomite.dg

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.google.common.util.concurrent.ServiceManager
import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.Stage
import kotlinx.coroutines.runBlocking
import net.dinomite.dg.emon.EmonClient
import net.dinomite.dg.emon.HttpEmonClient
import org.apache.commons.configuration2.CompositeConfiguration
import org.apache.commons.configuration2.PropertiesConfiguration
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder
import org.apache.commons.configuration2.builder.fluent.Parameters
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler
import org.mpierce.guice.warmup.GuiceWarmup
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Path
import java.time.Duration
import java.time.Instant
import java.util.concurrent.Future
import java.util.concurrent.FutureTask
import java.util.concurrent.TimeoutException

private val logger: Logger = LoggerFactory.getLogger("DataGatherer app")

private const val USAGE = """
USAGE:
    DataGathererKt CONFIG_DIR
"""
private val STOP_DURATION: Duration = Duration.ofSeconds(5)

fun main(args: Array<String>) {
    val start = Instant.now()

    val jacksonStartup = onDedicatedThread { configuredObjectMapper() }
    val startupFutures = listOf(
            onDedicatedThread { GuiceWarmup.warmUp() }
    )

    val config = buildConfiguration(args)

    startupFutures.forEach { it.get() }
    val injector = setupGuice(jacksonStartup.get(), config)

    val serviceManager = ServiceManager(listOf(
            injector.getInstance(HubitatToEmonReportingService::class.java),
            injector.getInstance(AwairToEmonReportingService::class.java)
    ))
    Runtime.getRuntime().addShutdownHook(shutdownHook(serviceManager))

    println("Startup: ${Duration.between(start, Instant.now()).toMillis()}ms")

    serviceManager.startAsync()
    serviceManager.awaitStopped()
}

private fun setupGuice(objectMapper: ObjectMapper, config: DataGathererConfig): Injector {
    return Guice.createInjector(
            Stage.PRODUCTION,
            object : AbstractModule() {
                override fun configure() {
                    bind(ObjectMapper::class.java).toInstance(objectMapper)

                    bind(EmonClient::class.java).toInstance(HttpEmonClient(objectMapper, config))

                    bind(DataGathererConfig::class.java).toInstance(config)
                    bind(HubitatToEmonReportingService::class.java)
                    bind(AwairToEmonReportingService::class.java)

                    binder().requireAtInjectOnConstructors()
                    binder().requireExactBindingAnnotations()
                    binder().requireExplicitBindings()
                    binder().disableCircularProxies()
                }
            }
    )
}

private fun <T> onDedicatedThread(builder: () -> T): Future<T> = FutureTask { builder() }.also { Thread(it).start() }

internal fun configuredObjectMapper() = ObjectMapper().apply {
    registerModule(JavaTimeModule())
    registerModule(KotlinModule())
    configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
}

private fun buildConfiguration(args: Array<String>): DataGathererConfig {
    val params = Parameters().properties().apply {
        setListDelimiterHandler(DefaultListDelimiterHandler(','))
    }

    return CompositeConfiguration().apply {
        val configPath = Path.of(args.getOrElse(0) { throw RuntimeException("CONFIG_DIR is required\n$USAGE") })
        Files.newDirectoryStream(configPath)
                .filter { it.toString().endsWith("properties") }
                .sortedDescending()
                .forEach { propertiesFile ->
                    this.addConfiguration(
                            FileBasedConfigurationBuilder(PropertiesConfiguration::class.java).apply {
                                configure(params.apply {
                                    setFileName(propertiesFile.toString())
                                })
                            }.configuration
                    )
                }
    }.let { DataGathererConfig(it) }
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
