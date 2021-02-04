package net.dinomite.gatherer.awair

import kotlinx.coroutines.runBlocking
import net.dinomite.gatherer.awair.AwairSensor.Comp.CO2
import net.dinomite.gatherer.model.Group
import net.dinomite.gatherer.model.Group.ENVIRONMENT
import net.dinomite.gatherer.model.Observation
import net.dinomite.gatherer.model.Sensor
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals

internal class AwairUpdateProducerTest {
    private fun awairClient(device: AwairDevice?) = object : AwairClient {
        override suspend fun retrieveDevice(deviceId: String) = device
    }

    @Test
    fun sensors() {
        val timestamp = Instant.parse("2021-01-25T09:15:00Z")
        val awairDevice = AwairDevice(
            listOf(AwairData(timestamp, 95, listOf(AwairSensor(CO2, 500.0))))
        )

        val awairUpdateProducer = AwairUpdateProducer(
            listOf("test-device"),
            awairClient(awairDevice)
        )

        val actual = runBlocking { awairUpdateProducer.sensors() }

        assertEquals(1, actual.size)
        val expected = Sensor(ENVIRONMENT, "awair_test-device_co2", Observation(500.0, timestamp))
        val sensor = actual.first()
        assertEquals(expected, sensor)
    }
}
