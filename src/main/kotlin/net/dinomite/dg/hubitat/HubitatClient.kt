package net.dinomite.dg.hubitat

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.coroutines.awaitObjectResult
import net.dinomite.dg.Settings
import net.dinomite.dg.objectMapper

class HubitatClient(config: Settings) {
    private val baseUrl = with(config) { "$HUBITAT_SCHEME://$HUBITAT_HOST$HUBITAT_DEVICE_BASE_PATH" }
    private val accessToken = config.HUBITAT_ACCESS_TOKEN

    suspend fun retrieveDevice(deviceId: String): Device {
        return Fuel.get(deviceUrl(deviceId))
                .awaitObjectResult(DeviceDeserializer)
                .fold(
                        { it },
                        { error -> throw Exception("An error of type ${error.exception} happened: ${error.message}") }
                )
    }

    private fun deviceUrl(deviceId: String) = "$baseUrl/$deviceId?access_token=$accessToken"
}

object DeviceDeserializer : ResponseDeserializable<Device> {
    override fun deserialize(content: String): Device = objectMapper.readValue(content, Device::class.java)
}
