package net.dinomite.gatherer.dp

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import kotlinx.coroutines.runBlocking
import net.dinomite.gatherer.createObjectMapper
import net.dinomite.gatherer.model.EqualsCircularFifoQueue
import net.dinomite.gatherer.model.Group.BATTERY
import net.dinomite.gatherer.model.Group.ENVIRONMENT
import net.dinomite.gatherer.model.Observation
import net.dinomite.gatherer.model.Sensor
import org.apache.commons.collections4.queue.CircularFifoQueue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.test.Test
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class DataProducerClientTest {
    private val wireMock = WireMockServer(WireMockConfiguration.options().dynamicPort())
    private val objectMapper = createObjectMapper()
    private val dataProducerClient by lazy {
        DataProducerClient(objectMapper, "http://localhost:${wireMock.port()}")
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
