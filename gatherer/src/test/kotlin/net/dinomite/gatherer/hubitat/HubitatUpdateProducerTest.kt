package net.dinomite.gatherer.hubitat

import kotlinx.coroutines.runBlocking
import net.dinomite.gatherer.hubitat.HubitatDevice.Attribute.DataType.NUMBER
import net.dinomite.gatherer.hubitat.HubitatDeviceType.ENVIRONMENT
import net.dinomite.gatherer.hubitat.HubitatDeviceType.POWER
import net.dinomite.gatherer.model.Group.ENERGY
import net.dinomite.gatherer.model.Observation
import net.dinomite.gatherer.model.Sensor
import kotlin.test.Test
import kotlin.test.assertEquals

internal class HubitatUpdateProducerTest {
    private val deviceId = 13

    private fun hubitatClient(device: HubitatDevice?) = object : HubitatClient {
        override suspend fun retrieveDevice(deviceId: String) = device
    }

    @Test
    fun sensorValues() {
        val hubitatUpdateProducer = HubitatUpdateProducer(
            mapOf("$deviceId" to POWER),
            hubitatClient(HubitatDevice(deviceId, "Foodevice", listOf(HubitatDevice.Attribute("power", "9", NUMBER))))
        )

        val actual = runBlocking { hubitatUpdateProducer.sensors() }

        assertEquals(1, actual.size)
        val expected = Sensor(ENERGY, "foodevice_power", Observation(9.0))
        val sensor = actual.first()
        assertEquals(expected.group, sensor.group)
        assertEquals(expected.name, sensor.name)
        assertEquals(expected.observations.first().value, sensor.observations.first().value)
    }

    @Test
    fun sensorValues_UnsupportedType() {
        val hubitatUpdateProducer = HubitatUpdateProducer(
            mapOf("$deviceId" to ENVIRONMENT),
            hubitatClient(HubitatDevice(deviceId, "Foodevice", listOf(HubitatDevice.Attribute("power", "9", NUMBER))))
        )

        val actual = runBlocking { hubitatUpdateProducer.sensors() }

        assertEquals(0, actual.size)
    }

    @Test
    fun sensorValues_NullDevice() {
        val hubitatUpdateProducer = HubitatUpdateProducer(
            mapOf("$deviceId" to POWER),
            hubitatClient(null)
        )

        val actual = runBlocking { hubitatUpdateProducer.sensors() }

        assertEquals(0, actual.size)
    }

    @Test
    fun sensorValues_NullPowerAmount() {
        val hubitatUpdateProducer = HubitatUpdateProducer(
            mapOf("$deviceId" to POWER),
            hubitatClient(HubitatDevice(deviceId, "Foodevice", listOf(HubitatDevice.Attribute("power", null, NUMBER))))
        )

        val actual = runBlocking { hubitatUpdateProducer.sensors() }

        assertEquals(0, actual.size)
    }
}
