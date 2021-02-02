package net.dinomite.gatherer.awair

import com.github.kittinunf.fuel.core.Headers
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.equalTo
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import kotlinx.coroutines.runBlocking
import net.dinomite.gatherer.testObjectMapper
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class AwairClientTest {
    private val wireMock = WireMockServer(WireMockConfiguration.options().dynamicPort())
    private val accessToken = "test-access-token"
    private val objectMapper = testObjectMapper()
    private val awairClient by lazy {
        AsyncAwairClient("http://localhost:${wireMock.port()}", accessToken, objectMapper)
    }

    @BeforeAll
    fun beforeAll() {
        wireMock.start()
    }

    @BeforeEach
    fun beforeEach() {
        wireMock.resetAll()
    }

    @Test
    fun retrieveDevice() = runBlocking {
        val deviceId = 13
        val deviceJson = """{
            "data": [
                {
                    "timestamp": "2021-01-25T09:15:00Z",
                    "score": 95,
                    "sensors": [{"comp": "co2", "value": 500}]
                }
            ]
        }""".trimIndent()

        wireMock.stubFor(
            get(urlEqualTo("/users/self/devices/awair-r2/$deviceId/air-data/latest?fahrenheit=true"))
                .withHeader(Headers.AUTHORIZATION, equalTo("Bearer $accessToken"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(deviceJson)
                )
        )

        val actual = awairClient.retrieveDevice("$deviceId")

        val expected = AwairDevice(
            listOf(
                AwairData(
                    Instant.parse("2021-01-25T09:15:00Z"),
                    95,
                    listOf(AwairSensor(AwairSensor.Comp.CO2, 500.0))
                )
            )
        )
        assertEquals(expected, actual)
    }
}
