package net.dinomite.dg.services

import kotlinx.coroutines.runBlocking
import net.dinomite.dg.emon.EmonClient
import net.dinomite.dg.emon.EmonNode
import net.dinomite.dg.emon.EmonUpdate
import org.junit.jupiter.api.Test
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
                lateinit var lastUpdate: Map<EmonNode, Map<String, String>>
                override suspend fun buildUpdate(): Map<EmonNode, Map<String, String>> {
                    lastUpdate = mapOf(EmonNode("node") to mapOf(Instant.now().toString() to "77"))
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

            val expected = client.queue.take()
            assertEquals(1, producer.lastUpdate.size)
            val first = producer.lastUpdate.entries.first()
            assertEquals(expected.node, first.key)
            assertEquals(expected.updates, first.value)
            ess.stopAsync()
        }
    }
}
