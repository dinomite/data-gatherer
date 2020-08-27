package net.dinomite.dg.awair

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Headers
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.coroutines.awaitObjectResponseResult
import com.google.inject.Inject
import net.dinomite.dg.DataGathererConfig
import org.slf4j.LoggerFactory

/**
 * GET /v1/users/self/devices/{{device_type}}/{{device_id}}/air-data/latest?fahrenheit=false HTTP/1.1
Host: developer-apis.awair.is
Authorization: Bearer example-token
 */
class AwairClient
@Inject constructor(objectMapper: ObjectMapper, config: DataGathererConfig) {
    private val deviceDeserializer = DeviceDeserializer(objectMapper)
    private val baseUrl = with(config) { "$awairScheme://$awairHost/$awairDeviceBasePath" }
    private val accessToken = config.awairAccessToken

    suspend fun retrieveDevice(deviceId: String): Device? {
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

    private fun deviceUrl(deviceId: String) = "$baseUrl/users/self/devices/awair-r2/$deviceId/air-data/latest?fahrenheit=true"

    companion object {
        private val logger = LoggerFactory.getLogger(AwairClient::class.java.name)
    }
}

class DeviceDeserializer(private val objectMapper: ObjectMapper) : ResponseDeserializable<Device> {
    override fun deserialize(content: String): Device = objectMapper.readValue(content, Device::class.java)
}
