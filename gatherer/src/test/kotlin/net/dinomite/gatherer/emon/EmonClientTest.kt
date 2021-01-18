package net.dinomite.gatherer.emon

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.anyUrl
import com.github.tomakehurst.wiremock.client.WireMock.equalTo
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import kotlinx.coroutines.runBlocking
import net.dinomite.gatherer.testObjectMapper
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import kotlin.test.Test
import kotlin.test.assertEquals

private val objectMapper = testObjectMapper()

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class EmonClientTest {
    private val wireMock = WireMockServer(WireMockConfiguration.options().dynamicPort())
    private val apiKey = "test-api-key"
    private val emonClient by lazy {
        HttpEmonClient(objectMapper, "http://localhost:${wireMock.port()}", apiKey)
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
    fun sendUpdate() = runBlocking {
        val update = EmonUpdate("sensor-group", mapOf("sensor-name" to "value"))

        wireMock.stubFor(
            get(anyUrl())
                .withQueryParam("apikey", equalTo(apiKey))
                .withQueryParam("node", equalTo(update.node))
                .withQueryParam("fulljson", equalTo(objectMapper.writeValueAsString(update.updates)))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withBody("""{"success":true}""")
                )
        )

        emonClient.sendUpdate(update)

        wireMock.verify(
            getRequestedFor(anyUrl())
                .withQueryParam("apikey", equalTo(apiKey))
                .withQueryParam("node", equalTo(update.node))
                .withQueryParam("fulljson", equalTo(objectMapper.writeValueAsString(update.updates)))
        )
    }
}

class EmonUpdateResponseDeserializerTest {
    private val emonUpdateResponseDeserializer = EmonUpdateResponseDeserializer(objectMapper)

    @Test
    fun deserialize() {
        val expected = EmonUpdateResponse(true, "foobar")
        val json = """{"success":true, "message":"foobar"}"""

        assertEquals(expected, emonUpdateResponseDeserializer.deserialize(json))
    }
}
