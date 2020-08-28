package net.dinomite.gatherer.hubitat

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.coroutines.awaitObjectResponseResult
import org.slf4j.LoggerFactory

interface HubitatClient {
    suspend fun retrieveDevice(deviceId: String): Device?
}

class AsyncHubitatClient(
        private val baseUrl: String,
        private val accessToken: String,
        objectMapper: ObjectMapper
) : HubitatClient {
    private val deviceDeserializer = DeviceDeserializer(objectMapper)

    override suspend fun retrieveDevice(deviceId: String): Device? {
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

    companion object {
        private val logger = LoggerFactory.getLogger(HubitatClient::class.java.name)
    }
}

class DeviceDeserializer(private val objectMapper: ObjectMapper) : ResponseDeserializable<Device> {
    override fun deserialize(content: String): Device = objectMapper.readValue(content, Device::class.java)
}
