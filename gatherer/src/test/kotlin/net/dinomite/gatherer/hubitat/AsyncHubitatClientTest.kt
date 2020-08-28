package net.dinomite.gatherer.hubitat

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import kotlinx.coroutines.runBlocking
import net.dinomite.gatherer.hubitat.Device.Attribute.DataType.NUMBER
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class AsyncHubitatClientTest {
    private val wireMock = WireMockServer()
    private val accessToken = "test-access-token"
    private val objectMapper = jacksonObjectMapper().apply {
        registerModule(JavaTimeModule())
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }
    private val hubitatClient by lazy {
        AsyncHubitatClient("http://localhost:${wireMock.port()}", accessToken, objectMapper)
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
            |"id": $deviceId,
            |"name": "foo",
            |"attributes": [{"name": "power", "currentValue": 7, "dataType": "NUMBER"}]
            |}""".trimMargin()

        stubFor(
                get(urlEqualTo("/$deviceId?access_token=$accessToken"))
                        .willReturn(
                                aResponse()
                                        .withStatus(200)
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(deviceJson)
                        )
        )

        val actual = hubitatClient.retrieveDevice("$deviceId")

        val expected = Device(deviceId, "foo", listOf(Device.Attribute("power", "7", NUMBER)))
        assertEquals(expected, actual)
    }
}
