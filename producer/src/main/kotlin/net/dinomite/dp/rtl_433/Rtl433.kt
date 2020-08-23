package net.dinomite.dp.rtl_433

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.jackson.JacksonConverter
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import net.dinomite.dg.emon.EmonNode
import net.dinomite.dp.DoubleSensor
import net.dinomite.dp.IntSensor
import net.dinomite.dp.Sensor
import org.apache.commons.configuration2.CompositeConfiguration
import org.apache.commons.configuration2.PropertiesConfiguration
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder
import org.apache.commons.configuration2.builder.fluent.Parameters
import org.slf4j.LoggerFactory
import org.slf4j.event.Level
import java.io.BufferedReader
import java.nio.file.Files
import java.nio.file.Path
import java.time.Duration
import java.time.Instant
import java.util.concurrent.CompletableFuture

private const val USAGE = """
USAGE:
    Rtl433 CONFIG_DIR
"""

object Rtl433 {
    private val logger = LoggerFactory.getLogger(Rtl433::class.java.name)

    lateinit var objectMapper: ObjectMapper

    private val nodeData = mutableMapOf<String, Sensor>()

    @JvmStatic
    fun main(args: Array<String>) {
        val start = Instant.now()

        val jacksonFuture = CompletableFuture.supplyAsync {
            jacksonObjectMapper()
                    .registerModule(JavaTimeModule())
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        }
        val config = CompletableFuture.supplyAsync {
            buildConfiguration(args)
        }

        val server = embeddedServer(Netty, 8080) {
            install(CallLogging) { level = Level.INFO }

            install(Routing) {
                get("/") {
                    call.respond(mapOf(EmonNode("environment") to nodeData.values))
                }
            }

            install(ContentNegotiation) {
                objectMapper = jacksonFuture.get()
                register(ContentType.Application.Json, JacksonConverter(objectMapper))
            }
        }

        server.start(wait = false)

        logger.info("Startup time: ${Duration.between(start, Instant.now())}")

        // TODO remove sensors that don't reply for a while
        val input = runCommand(config.get().rtl433Command)
        input.forEachLine { line ->
            objectMapper.readValue<RtlData>(line)
                    .toSensors()
                    .forEach {
                        nodeData[it.name()] = it
                    }
            println(nodeData)
        }
    }
}

// TODO factor out & share with gatherer
private fun buildConfiguration(args: Array<String>): Rtl433Config {
    return CompositeConfiguration().apply {
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
    }.let { Rtl433Config(it) }
}

fun runCommand(command: String): BufferedReader {
    return ProcessBuilder(*command.split("\\s".toRegex()).toTypedArray())
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectError(ProcessBuilder.Redirect.PIPE)
            .start()
            .inputStream
            .bufferedReader()
}

data class RtlData(
        @JsonProperty("brand") val brand: String?,
        @JsonProperty("model") val model: String,
        @JsonProperty("id") val id: Int,
        @JsonProperty("battery_ok") val batteryOk: Int,
        // TODO convert to fahrenheit
        @JsonProperty("temperature_C") val temperatureC: Double?,
        @JsonProperty("humidity") val humidity: Int?
) {
    private fun sensorKey(subSensor: String) = if (brand != null) {
        "$brand-$model-$id-$subSensor"
    } else {
        "$model-$id-$subSensor"
    }

    fun toSensors(): List<Sensor> {
        val sensors = mutableListOf<Sensor>()
        if (temperatureC != null) {
            sensors.add(DoubleSensor(sensorKey("temperature"), temperatureC))
        }
        if (humidity != null) {
            sensors.add(IntSensor(sensorKey("humidity"), humidity))
        }
        return sensors
    }
}
