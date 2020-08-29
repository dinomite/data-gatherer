package net.dinomite.producer

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
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
        val config = CompletableFuture.supplyAsync {
            buildConfiguration(args, USAGE) { Rtl433Config(it) }
        }

        val server = embeddedServer(Netty, 8080) {
            install(StatusPages) {
                exception<Throwable> { cause ->
                    call.respond(HttpStatusCode.InternalServerError)
                    logger.debug("Unable to handle request", cause)
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

        val input = runCommand(config.get().rtl433Command)
        input.forEachLine { line ->
            objectMapper.readValue<RtlData>(line)
                    .toSensorUpdates()
                    .forEach {
                        nodeDataManager.updateNode(it)
                    }
            println(nodeDataManager.getValues())
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
