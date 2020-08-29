package net.dinomite.gatherer.services

import com.google.common.eventbus.EventBus
import com.google.common.util.concurrent.AbstractScheduledService
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import java.time.Duration
import java.time.Duration.ZERO
import kotlin.system.measureTimeMillis

/**
 * Service that runs at the given period after no delay.  Each iteration
 * pulls an update from the producer and sends it to the EventBus
 */
@Suppress("UnstableApiUsage")
abstract class EventBusScheduledService(private val period: Duration,
                                        private val eventBus: EventBus,
                                        private val producer: UpdateProducer) : AbstractScheduledService() {
    override fun scheduler(): Scheduler = Scheduler.newFixedRateSchedule(ZERO, period)

    override fun runOneIteration() = runBlocking {
        val time = measureTimeMillis {
            producer.sensors()
                    .forEach {
                        logger.debug("Posting: {}", it)
                        eventBus.post(it)
                    }
        }
        logger.info("Gathering data took $time ms")
    }

    private val logger = LoggerFactory.getLogger(this::class.java.name)
}
