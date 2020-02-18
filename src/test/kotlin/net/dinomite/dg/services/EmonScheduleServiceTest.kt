package net.dinomite.dg.services

import kotlinx.coroutines.runBlocking
import net.dinomite.dg.emon.EmonClient
import net.dinomite.dg.emon.EmonUpdate
import org.junit.Test
import java.time.Duration
import java.time.Instant
import java.util.concurrent.ArrayBlockingQueue
import kotlin.test.assertEquals

@Suppress("UnstableApiUsage")
internal class EmonScheduleServiceTest {
    @Suppress("BlockingMethodInNonBlockingContext")
    @Test
    fun runOneIteration() {
        runBlocking {
            val producer = object : EmonUpdateProducer {
                lateinit var lastUpdate: EmonUpdate
                override suspend fun buildUpdate(): EmonUpdate {
                    lastUpdate = EmonUpdate(Instant.now().toString(), mapOf())
                    return lastUpdate
                }
            }
            val client = object : EmonClient {
                val queue = ArrayBlockingQueue<EmonUpdate>(1)
                override suspend fun sendUpdate(update: EmonUpdate) {
                    queue.offer(update)
                }
            }
            val ess = EmonScheduleService(Duration.ofMillis(15), producer, client)

            ess.startAsync()

            assertEquals(client.queue.take(), producer.lastUpdate)
            ess.stopAsync()
        }
    }
}