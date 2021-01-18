package net.dinomite.gatherer.emon

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import kotlinx.coroutines.runBlocking
import net.dinomite.gatherer.model.Group
import net.dinomite.gatherer.model.Group.ENVIRONMENT
import net.dinomite.gatherer.model.Observation
import net.dinomite.gatherer.model.Sensor
import kotlin.test.Test

internal class EmonReporterTest {
    private val emonClient: EmonClient = mock()
    private val emonReporter = EmonReporter(emonClient)

    @Test
    fun sendSensorDataToEmon() = runBlocking {
        val sensor = Sensor(ENVIRONMENT, "foo", Observation(7))

        emonReporter.sendSensorDataToEmon(sensor)

        verify(emonClient).sendUpdate(EmonUpdate(
            sensor.group.reportingValue(),
            mapOf(sensor.name to "${sensor.observations.first().value}")
        ))
    }
}
