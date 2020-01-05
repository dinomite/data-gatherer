package net.dinomite.dg.hubitat

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.coroutines.awaitObjectResponseResult
import net.dinomite.dg.DataGathererConfig
import net.dinomite.dg.objectMapper
import org.slf4j.LoggerFactory

class HubitatClient(config: DataGathererConfig) {
    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.name)
    }

    private val baseUrl = with(config) { "$HUBITAT_SCHEME://$HUBITAT_HOST/$HUBITAT_DEVICE_BASE_PATH" }
    private val accessToken = config.HUBITAT_ACCESS_TOKEN

    suspend fun retrieveDevice(deviceId: String): Device? {
        val (request, _, result) = Fuel.get(deviceUrl(deviceId))
                .awaitObjectResponseResult(DeviceDeserializer)
        return result.fold(
                { it },
                { error ->
                    logger.warn("Request to ${request.url} got error ${error.exception}: ${error.message}")
                    null
                }
        )
    }

    private fun deviceUrl(deviceId: String) = "$baseUrl/$deviceId?access_token=$accessToken"
}

object DeviceDeserializer : ResponseDeserializable<Device> {
    override fun deserialize(content: String): Device = objectMapper.readValue(content, Device::class.java)
}
