package net.dinomite.gatherer.emon

import kotlinx.coroutines.runBlocking
import net.dinomite.gatherer.model.Group.ENVIRONMENT
import net.dinomite.gatherer.model.Observation
import net.dinomite.gatherer.model.Sensor
import kotlin.test.Test
import kotlin.test.assertEquals

internal class EmonReporterTest {
    private lateinit var receivedUpdate: EmonUpdate
    private val emonClient: EmonClient = object : EmonClient {
        override suspend fun sendUpdate(update: EmonUpdate) {
            receivedUpdate = update
        }
    }
    private val emonReporter = EmonReporter(emonClient)

    @Test
    fun sendSensorDataToEmon() = runBlocking {
        val sensor = Sensor(ENVIRONMENT, "foo", Observation(7))

        emonReporter.sendSensorDataToEmon(sensor)

        assertEquals(
            EmonUpdate(
                sensor.group.reportingValue(),
                mapOf(sensor.name to "${sensor.observations.first().value}")
            ),
            receivedUpdate
        )
    }
}
