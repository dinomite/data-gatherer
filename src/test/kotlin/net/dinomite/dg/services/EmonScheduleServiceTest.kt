package net.dinomite.dg.services

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import net.dinomite.dg.emon.EmonClient
import net.dinomite.dg.emon.EmonUpdate
import org.junit.Test
import java.time.Duration
import java.time.Instant
import kotlin.test.assertEquals

@Suppress("UnstableApiUsage")
internal class EmonScheduleServiceTest {
    @Test
    fun runOneIteration() = runBlocking {
        val period = Duration.ofMillis(10)
        val producer = object : EmonUpdateProducer {
            lateinit var lastUpdate: EmonUpdate
            override suspend fun buildUpdate(): EmonUpdate {
                lastUpdate = EmonUpdate(Instant.now().toString(), mapOf())
                return lastUpdate
            }
        }
        val client = object : EmonClient {
            lateinit var lastUpdate: EmonUpdate
            override suspend fun sendUpdate(update: EmonUpdate) {
                lastUpdate = update
            }
        }
        val ess = EmonScheduleService(period, producer, client)

        ess.startAsync()
        delay(period.toMillis() * 2)
        ess.stopAsync()

        assertEquals(client.lastUpdate, producer.lastUpdate)
    }
}