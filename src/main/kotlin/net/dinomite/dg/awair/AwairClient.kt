package net.dinomite.dg.awair

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Headers
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.coroutines.awaitObjectResponseResult
import net.dinomite.dg.DataGathererConfig
import net.dinomite.dg.objectMapper
import org.slf4j.LoggerFactory

/**
 * GET /v1/users/self/devices/{{device_type}}/{{device_id}}/air-data/latest?fahrenheit=false HTTP/1.1
Host: developer-apis.awair.is
Authorization: Bearer example-token
 */
class AwairClient(config: DataGathererConfig) {
    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.name)
    }

    private val baseUrl = with(config) { "$AWAIR_SCHEME://$AWAIR_HOST/$AWAIR_DEVICE_BASE_PATH" }
    private val accessToken = config.AWAIR_ACCESS_TOKEN

    suspend fun retrieveDevice(deviceId: String): Device? {
        val (request, _, result) = Fuel.get(deviceUrl(deviceId))
                .header(Headers.AUTHORIZATION, "Bearer $accessToken")
                .awaitObjectResponseResult(DeviceDeserializer)
        return result.fold(
                { it },
                { error ->
                    logger.warn("Request to ${request.url} got error ${error.exception}: ${error.message}")
                    null
                }
        )
    }

    private fun deviceUrl(deviceId: String) = "$baseUrl/users/self/devices/awair-r2/$deviceId/air-data/latest?fahrenheit=true"
}

object DeviceDeserializer : ResponseDeserializable<Device> {
    override fun deserialize(content: String): Device = objectMapper.readValue(content, Device::class.java)
}
