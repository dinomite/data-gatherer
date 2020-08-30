@file:Suppress("UnstableApiUsage")

package net.dinomite.gatherer

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.google.common.eventbus.AsyncEventBus
import com.google.common.eventbus.EventBus
import com.google.common.util.concurrent.ServiceManager
import com.google.common.util.concurrent.ThreadFactoryBuilder
import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.Stage
import kotlinx.coroutines.runBlocking
import net.dinomite.gatherer.config.buildConfiguration
import net.dinomite.gatherer.emon.EmonClient
import net.dinomite.gatherer.emon.EmonReporter
import net.dinomite.gatherer.emon.HttpEmonClient
import org.mpierce.guice.warmup.GuiceWarmup
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration
import java.time.Instant
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.FutureTask
import java.util.concurrent.TimeoutException

private val logger: Logger = LoggerFactory.getLogger("DataGatherer app")

private const val USAGE = """
USAGE:
    DataGathererKt CONFIG_DIR
"""

fun main(args: Array<String>) {
    val start = Instant.now()

    val jacksonStartup = onDedicatedThread {
        jacksonObjectMapper()
                .registerModule(JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
    }
    val configStartup = onDedicatedThread {
        buildConfiguration(args, USAGE) { DataGathererConfig(it) }
    }
    val startupFutures = listOf(
            onDedicatedThread { GuiceWarmup.warmUp() }
    )

    startupFutures.forEach { it.get() }

    val injector = setupGuice(jacksonStartup.get(), configStartup.get())

    injector.getInstance(EventBus::class.java).register(injector.getInstance(EmonReporter::class.java))

    val serviceManager = ServiceManager(listOf(
            injector.getInstance(HubitatReportingService::class.java),
            injector.getInstance(AwairToEmonReportingService::class.java),
            injector.getInstance(DataProducerReportingService::class.java)
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
                    bind(EmonReporter::class.java)
                    bind(EmonClient::class.java).to(HttpEmonClient::class.java)
                    bind(HubitatReportingService::class.java)
                    bind(AwairToEmonReportingService::class.java)
                    bind(DataProducerReportingService::class.java)

                    bind(ObjectMapper::class.java).toInstance(objectMapper)
                    bind(DataGathererConfig::class.java).toInstance(config)

                    bind(EventBus::class.java).toInstance(AsyncEventBus(Executors.newCachedThreadPool(
                            ThreadFactoryBuilder().setNameFormat("event-bus-%d").build()
                    )))

                    binder().requireAtInjectOnConstructors()
                    binder().requireExactBindingAnnotations()
                    binder().requireExplicitBindings()
                    binder().disableCircularProxies()
                }
            }
    )
}

private fun <T> onDedicatedThread(builder: () -> T): Future<T> = FutureTask { builder() }.also { Thread(it).start() }

private fun shutdownHook(serviceManager: ServiceManager) = object : Thread() {
    override fun run() = runBlocking {
        logger.info("Shutting down")
        try {
            serviceManager.stopAsync().awaitStopped(Duration.ofSeconds(5))
        } catch (e: TimeoutException) {
            logger.warn("Timed out while stopping services", e)
        }
        logger.info("Done")
    }
}
