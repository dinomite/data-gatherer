package net.dinomite.gatherer.dp

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import kotlinx.coroutines.runBlocking
import net.dinomite.gatherer.model.EqualsCircularFifoQueue
import net.dinomite.gatherer.model.Group.BATTERY
import net.dinomite.gatherer.model.Group.ENVIRONMENT
import net.dinomite.gatherer.model.Observation
import net.dinomite.gatherer.model.Sensor
import net.dinomite.gatherer.testObjectMapper
import org.apache.commons.collections4.queue.CircularFifoQueue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.test.Test
import kotlin.test.assertEquals

private val objectMapper = testObjectMapper()

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class DataProducerClientTest {
    private val wireMock = WireMockServer(WireMockConfiguration.options().dynamicPort())
    private val dataProducerClient by lazy {
        AsyncDataProducerClient(objectMapper, "http://localhost:${wireMock.port()}")
    }

    private val json = javaClass.getResource("/net/dinomite/gatherer/dp/producer.json").readText()

    @BeforeAll
    fun beforeAll() {
        wireMock.start()
    }

    @BeforeEach
    fun beforeEach() {
        wireMock.resetAll()
    }

    @Test
    fun retrieveData() = runBlocking {
        val expected = listOf(
            Sensor(
                ENVIRONMENT,
                "Acurite-Tower-11028-temperature",
                EqualsCircularFifoQueue(
                    CircularFifoQueue<Observation<*>>().apply {
                        add(Observation(74.84, Instant.ofEpochMilli(1610916410822).plus(617, ChronoUnit.MICROS)))
                        add(Observation(74.84, Instant.ofEpochMilli(1610916410824).plus(274, ChronoUnit.MICROS)))
                        add(Observation(74.84, Instant.ofEpochMilli(1610916427599).plus(74, ChronoUnit.MICROS)))
                    }
                )
            ),
            Sensor(
                ENVIRONMENT,
                "Acurite-Tower-11028-humidity",
                Observation(36, Instant.ofEpochMilli(1610916410822).plus(617, ChronoUnit.MICROS))
            ),
            Sensor(
                BATTERY,
                "Acurite-Tower-11028-battery",
                Observation(1, Instant.ofEpochMilli(1610916410822).plus(617, ChronoUnit.MICROS))
            ),
        )
        wireMock.stubFor(
            get(urlEqualTo("/"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(json)
                )
        )

        val actual = dataProducerClient.retrieveData()!!
        assertEquals(expected.size, actual.size)
        assertEquals(expected[0], actual[0])
        assertEquals(expected[1], actual[1])
        assertEquals(expected[2], actual[2])
    }
}

internal class SensorsDeserializerTest {
    private val sensorsDeserializer = SensorsDeserializer(testObjectMapper())

    @Test
    fun deserialize() {
        val timestamp = Instant.parse("2020-08-30T14:30:00Z")
        val expected = listOf(
            Sensor(ENVIRONMENT, "Acurite-Tower-12090-temperature", Observation(73.75, timestamp)),
            Sensor(ENVIRONMENT, "Acurite-Tower-12090-humidity", Observation(54, timestamp)),
            Sensor(ENVIRONMENT, "OS-Oregon-THN132N-25-temperature", Observation(91.4, timestamp))
        )

        val json = """[
                {
                    "group": "ENVIRONMENT",
                    "name": "Acurite-Tower-12090-temperature",
                    "observations": [
                        {
                            "value": 73.75,
                            "timestamp": "2020-08-30T14:30:00Z"
                        }
                    ]
                },
                {
                    "group": "ENVIRONMENT",
                    "name": "Acurite-Tower-12090-humidity",
                    "observations": [
                        {
                            "value": 54,
                            "timestamp": "2020-08-30T14:30:00Z"
                        }
                    ]
                },
                {
                    "group": "ENVIRONMENT",
                    "name": "OS-Oregon-THN132N-25-temperature",
                    "observations": [
                        {
                            "value": 91.4,
                            "timestamp": "2020-08-30T14:30:00Z"
                        }
                    ]
                }
        ]"""

        assertEquals(expected, sensorsDeserializer.deserialize(json))
    }
}
