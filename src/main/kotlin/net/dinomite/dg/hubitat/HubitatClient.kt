package net.dinomite.dg.hubitat

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.coroutines.awaitObjectResponseResult
import net.dinomite.dg.DataGathererConfig
import org.slf4j.LoggerFactory

class HubitatClient(objectMapper: ObjectMapper, config: DataGathererConfig) {
    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.name)
    }

    private val deviceDeserializer = DeviceDeserializer(objectMapper)
    private val baseUrl = with(config) { "$hubitatScheme://$hubitatHost/$hubitatDeviceBasePath" }
    private val accessToken = config.hubitatAccessToken

    suspend fun retrieveDevice(deviceId: String): Device? {
        val (request, _, result) = Fuel.get(deviceUrl(deviceId))
                .awaitObjectResponseResult(deviceDeserializer)
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

class DeviceDeserializer(private val objectMapper: ObjectMapper) : ResponseDeserializable<Device> {
    override fun deserialize(content: String): Device = objectMapper.readValue(content, Device::class.java)
}
