package net.dinomite.gatherer.awair

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Headers
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.coroutines.awaitObjectResponseResult
import org.slf4j.LoggerFactory

interface AwairClient {
    suspend fun retrieveDevice(deviceId: String): AwairDevice?
}

/**
 * GET /v1/users/self/devices/{{device_type}}/{{device_id}}/air-data/latest?fahrenheit=false HTTP/1.1
Host: developer-apis.awair.is
Authorization: Bearer example-token
 */
class AsyncAwairClient(
    private val baseUrl: String,
    private val accessToken: String,
    objectMapper: ObjectMapper
) : AwairClient {
    private val deviceDeserializer = DeviceDeserializer(objectMapper)

    override suspend fun retrieveDevice(deviceId: String): AwairDevice? {
        val (request, _, result) = Fuel.get(deviceUrl(deviceId))
            .header(Headers.AUTHORIZATION, "Bearer $accessToken")
            .awaitObjectResponseResult(deviceDeserializer)
        return result.fold(
            { it },
            { error ->
                logger.warn("Request to ${request.url} got error ${error.exception}: ${error.message}")
                null
            }
        )
    }

    private fun deviceUrl(deviceId: String) =
        "$baseUrl/users/self/devices/awair-r2/$deviceId/air-data/latest?fahrenheit=true"

    companion object {
        private val logger = LoggerFactory.getLogger(AsyncAwairClient::class.java.name)
    }
}

class DeviceDeserializer(private val objectMapper: ObjectMapper) : ResponseDeserializable<AwairDevice> {
    override fun deserialize(content: String): AwairDevice = objectMapper.readValue(content, AwairDevice::class.java)
}
