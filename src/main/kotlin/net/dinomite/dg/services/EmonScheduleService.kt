package net.dinomite.dg.services

import com.google.common.util.concurrent.AbstractScheduledService
import kotlinx.coroutines.runBlocking
import net.dinomite.dg.DataGathererConfig
import net.dinomite.dg.emon.EmonClient
import org.slf4j.LoggerFactory
import java.time.Duration.ZERO
import kotlin.system.measureTimeMillis

@Suppress("UnstableApiUsage")
class EmonScheduleService(private val config: DataGathererConfig,
                          private val producer: EmonUpdateProducer,
                          private val emonClient: EmonClient) : AbstractScheduledService() {
    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.name)
    }

    override fun scheduler(): Scheduler = Scheduler.newFixedRateSchedule(ZERO, config.SCHEDULED_SERVICE_INTERVAL)

    override fun runOneIteration() = runBlocking {
        val time = measureTimeMillis {
            emonClient.sendUpdate(producer.buildUpdate())
        }
        logger.debug("Iteration took $time ms")
    }
}
