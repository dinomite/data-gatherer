package net.dinomite.dg.services.hubitat

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.coroutines.awaitObjectResult
import com.google.common.util.concurrent.AbstractScheduledService
import kotlinx.coroutines.runBlocking
import net.dinomite.dg.Settings
import net.dinomite.dg.objectMapper
import net.dinomite.dg.services.hubitat.Device.Attribute.DataType.NUMBER
import java.time.Duration.ZERO

@Suppress("UnstableApiUsage")
class HubitatScheduleService(private val config: Settings,
                             private val objectMapper: ObjectMapper) : AbstractScheduledService() {
    private val baseUrl = with(config) { "$HUBITAT_SCHEME://$HUBITAT_HOST$HUBITAT_DEVICE_BASE_PATH" }
    private val devices = config.HUBITAT_DEVICES
    private val accessToken = config.HUBITAT_ACCESS_TOKEN

    override fun scheduler(): Scheduler = Scheduler.newFixedRateSchedule(ZERO, config.SCHEDULED_SERVICE_INTERVAL)

    override fun runOneIteration() = runBlocking {
        devices.forEach { deviceId ->
            val url = "$baseUrl/$deviceId?access_token=$accessToken"
            Fuel.get(url)
                    .awaitObjectResult(DeviceDeserializer)
                    .fold(
                            { device ->
                                val power = device.attributes.filter { it.name == "power" }.first().intValue()
                                println("Power usage for ${device.name}: $power")
                            },
                            { error -> println("An error of type ${error.exception} happened: ${error.message}") }
                    )

            // TODO send to emoncms
            // http "https://emon.dinomite.net/input/post?node=emontx&apikey=99c6d3081d68d0dd5640b0c8f5e1bcb8&fulljson={\"portch_switch_power\":$VALUE}" > /dev/null
        }
    }
}

data class Device(val id: Int, val name: String, val attributes: List<Attribute>) {
    data class Attribute(val name: String,
                         val currentValue: String?,
                         val dataType: DataType) {
        enum class DataType(val value: String) {
            NUMBER("NUMBER"),
            STRING("STRING"),
            ENUM("ENUM")
        }

        fun intValue(): Int? {
            if (dataType != NUMBER) {
                throw IllegalAccessError("DataType is $dataType, not $NUMBER")
            }

            return currentValue?.toInt()
        }
    }
}

object DeviceDeserializer : ResponseDeserializable<Device> {
    override fun deserialize(content: String): Device = objectMapper.readValue(content, Device::class.java)
}