package net.dinomite.gatherer.hubitat

import kotlinx.coroutines.runBlocking
import net.dinomite.gatherer.hubitat.Device.Attribute.DataType.NUMBER
import net.dinomite.gatherer.hubitat.DeviceType.POWER
import net.dinomite.gatherer.model.DoubleValue
import net.dinomite.gatherer.model.Group.ENERGY
import net.dinomite.gatherer.model.Sensor
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class HubitatUpdateProducerTest {
    private val id = 13
    private val hubitatClient = object : HubitatClient {
        override suspend fun retrieveDevice(deviceId: String): Device? {
            return Device(id, "Foodevice", listOf(Device.Attribute("power", "9", NUMBER)))
        }
    }

    private val hubitatUpdateProducer = HubitatUpdateProducer(mapOf("$id" to POWER), hubitatClient)

    @Test
    fun sensorValues() {
        val actual = runBlocking { hubitatUpdateProducer.sensors() }
        val expected = listOf(Sensor(ENERGY, "foodevice_power", DoubleValue(9.0)))
        assertEquals(expected, actual)
    }
}
