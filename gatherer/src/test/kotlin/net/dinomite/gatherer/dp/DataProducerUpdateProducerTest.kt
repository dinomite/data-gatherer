package net.dinomite.gatherer.dp

import kotlinx.coroutines.runBlocking
import net.dinomite.gatherer.model.Group
import net.dinomite.gatherer.model.Observation
import net.dinomite.gatherer.model.Sensor
import java.time.Duration
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals

internal class DataProducerUpdateProducerTest {
    private fun dataProducerClient(sensors: List<Sensor>?) = object : DataProducerClient {
        override suspend fun retrieveData(): List<Sensor>? {
            return sensors
        }
    }

    @Test
    fun `Single client with one sensor`() = runBlocking {
        val sensor = Sensor(Group.ENVIRONMENT, "the-sensor", Observation(7))
        val client = dataProducerClient(listOf(sensor))
        val dataProducerUpdateProducer = DataProducerUpdateProducer(listOf(client))

        val result = dataProducerUpdateProducer.sensors()

        assertEquals(listOf(sensor), result)
    }

    @Test
    fun `Single client with multiple sensors`() = runBlocking {
        val sensor1 = Sensor(Group.ENVIRONMENT, "the-sensor", Observation(7))
        val sensor2 = Sensor(Group.ENVIRONMENT, "another-sensor", Observation(9))
        val client = dataProducerClient(listOf(sensor1, sensor2))
        val dataProducerUpdateProducer = DataProducerUpdateProducer(listOf(client))

        val result = dataProducerUpdateProducer.sensors()

        assertEquals(listOf(sensor1, sensor2), result)
    }

    @Test
    fun `Single client with multiple sensors, only one with recent updates`() = runBlocking {
        val sensor1 = Sensor(Group.ENVIRONMENT, "foo", Observation(7))
        val sensor2 = Sensor(Group.ENVIRONMENT, "bar", Observation(9, Instant.now().minus(Duration.ofMinutes(30))))
        val client = dataProducerClient(listOf(sensor1, sensor2))
        val dataProducerUpdateProducer = DataProducerUpdateProducer(listOf(client))

        val result = dataProducerUpdateProducer.sensors()

        assertEquals(listOf(sensor1), result)
    }

    @Test
    fun `Multiple clients`() = runBlocking {
        val sensor1 = Sensor(Group.ENVIRONMENT, "foo", Observation(7))
        val sensor2 = Sensor(Group.ENVIRONMENT, "bar", Observation(9))
        val client1 = dataProducerClient(listOf(sensor1))
        val client2 = dataProducerClient(listOf(sensor2))
        val dataProducerUpdateProducer = DataProducerUpdateProducer(listOf(client1, client2))

        val result = dataProducerUpdateProducer.sensors()

        assertEquals(listOf(sensor1, sensor2), result)
    }
}
