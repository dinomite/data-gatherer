package net.dinomite.producer

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.JacksonConverter
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import net.dinomite.gatherer.config.buildConfiguration
import net.dinomite.producer.model.DataProducerResponse
import net.dinomite.producer.model.RtlData
import org.slf4j.LoggerFactory
import org.slf4j.event.Level
import java.io.BufferedReader
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

    private val nodeDataManager = NodeDataManager()

    @JvmStatic
    fun main(args: Array<String>) {
        val start = Instant.now()

        val jacksonFuture = CompletableFuture.supplyAsync {
            jacksonObjectMapper()
                .registerModule(JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        }
        val configFuture = CompletableFuture.supplyAsync {
            buildConfiguration(args, USAGE) { Rtl433Config(it) }
        }

        val server = embeddedServer(Netty, 8080) {
            install(StatusPages) {
                exception<Throwable> { cause ->
                    call.respond(HttpStatusCode.InternalServerError)
                    logger.warn("Unable to handle request", cause)
                    throw cause
                }
            }

            install(CallLogging) { level = Level.INFO }

            install(ContentNegotiation) {
                objectMapper = jacksonFuture.get()
                register(ContentType.Application.Json, JacksonConverter(objectMapper))
            }

            install(Routing) {
                get("/") {
                    call.respond(DataProducerResponse(nodeDataManager.getValues()))
                }
            }
        }

        server.start(wait = false)

        logger.info("Startup time: ${Duration.between(start, Instant.now())}")

        val config = configFuture.get()
        val sensorAllowList = config.allowedSensors(objectMapper)

        for (line in runCommand(config.rtl433Command).lines()) {
            val rtlData = try {
                objectMapper.readValue<RtlData>(line)
            } catch (e: JsonProcessingException) {
                logger.warn("Couldn't process JSON", e)
                continue
            }

            if (sensorAllowList.contains(rtlData.sensorName())) {
                rtlData
                    .toSensorUpdates()
                    .forEach {
                        nodeDataManager.updateNode(it)
                    }
                logger.debug(nodeDataManager.getValues().toString())
            }
        }
    }
}

fun runCommand(command: String): BufferedReader {
    return ProcessBuilder(*command.split("\\s".toRegex()).toTypedArray())
        .redirectOutput(ProcessBuilder.Redirect.PIPE)
        .redirectError(ProcessBuilder.Redirect.PIPE)
        .start()
        .inputStream
        .bufferedReader()
}
