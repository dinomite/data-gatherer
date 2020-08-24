package net.dinomite.dg.services

import com.google.common.util.concurrent.AbstractScheduledService
import kotlinx.coroutines.runBlocking
import net.dinomite.dg.emon.EmonClient
import net.dinomite.dg.emon.EmonUpdate
import org.slf4j.LoggerFactory
import java.time.Duration
import java.time.Duration.ZERO
import kotlin.system.measureTimeMillis

/**
 * Service that runs at the given period after no delay.  Each iteration
 * pulls an update from the producer and sends it with the emonClient.
 */
@Suppress("UnstableApiUsage")
abstract class EmonScheduleService(private val period: Duration,
                                   private val producer: EmonUpdateProducer,
                                   private val emonClient: EmonClient) : AbstractScheduledService() {
    private val logger = LoggerFactory.getLogger(this::class.java.name)

    override fun scheduler(): Scheduler = Scheduler.newFixedRateSchedule(ZERO, period)

    override fun runOneIteration() = runBlocking {
        logger.info("Starting update…")
        val time = measureTimeMillis {
            producer.buildUpdates().forEach { update ->
                update.forEach { (node, map) ->
                    emonClient.sendUpdate(EmonUpdate(node, map))
                }
            }
        }
        logger.info("Iteration took $time ms")
    }
}