package net.dinomtie.gatherer.services

import com.google.common.eventbus.AsyncEventBus
import com.google.common.eventbus.Subscribe
import kotlinx.coroutines.runBlocking
import net.dinomite.gatherer.model.Group
import net.dinomite.gatherer.model.IntSensor
import net.dinomite.gatherer.model.Sensor
import net.dinomite.gatherer.services.EventBusScheduledService
import net.dinomite.gatherer.services.UpdateProducer
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.Instant
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.Executors
import kotlin.test.assertEquals

@Suppress("UnstableApiUsage")
internal class EventBusScheduledServiceTest {
    @Suppress("BlockingMethodInNonBlockingContext")
    @Test
    fun runOneIteration() {
        runBlocking {
            // A producer that stores it's last update in an ABC, halting further updates until that is take()-en
            val producer = object : UpdateProducer {
                val lastUpdate = ArrayBlockingQueue<Sensor>(1)
                override suspend fun sensorValues(): List<Sensor> {
                    val update = IntSensor(Group.ENVIRONMENT, Instant.now().toString(), 77)
                    lastUpdate.offer(update)
                    return listOf(update)
                }
            }
            val eventBus = AsyncEventBus(Executors.newCachedThreadPool())
            val ess = object : EventBusScheduledService(Duration.ofMillis(1500), eventBus, producer) {}

            // A subscriber that puts received events onto an ABC, halting further events until that is take()-en
            val subscriber = object {
                val received = ArrayBlockingQueue<Sensor>(1)

                @Subscribe
                fun handle(sensor: Sensor) {
                    received.offer(sensor)
                }
            }
            eventBus.register(subscriber)

            ess.startAsync()
            ess.awaitRunning()

            val expected = producer.lastUpdate.take()
            val actual = subscriber.received.take()

            assertEquals(expected, actual)

            ess.stopAsync().awaitTerminated()
        }
    }
}
