package net.dinomite.dg.services

import com.google.common.util.concurrent.AbstractScheduledService
import kotlinx.coroutines.runBlocking
import net.dinomite.dg.emon.EmonClient
import org.slf4j.LoggerFactory
import java.time.Duration
import java.time.Duration.ZERO
import kotlin.system.measureTimeMillis

/**
 * Service that runs at the given period after no delay.  Each iteration
 * pulls an update from the producer and sends it with the emonClient.
 */
@Suppress("UnstableApiUsage")
class EmonScheduleService(private val period: Duration,
                          private val producer: EmonUpdateProducer,
                          private val emonClient: EmonClient) : AbstractScheduledService() {
    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.name)
    }

    override fun scheduler(): Scheduler = Scheduler.newFixedRateSchedule(ZERO, period)

    override fun runOneIteration() = runBlocking {
        val time = measureTimeMillis {
            emonClient.sendUpdate(producer.buildUpdate())
        }
        logger.debug("Iteration took $time ms")
    }
}
