package net.dinomite.dg.emon

import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import kotlinx.coroutines.runBlocking
import net.dinomite.dp.model.Sensor
import org.slf4j.LoggerFactory
import kotlin.system.measureTimeMillis

@Suppress("UnstableApiUsage")
class EmonReporter(private val emonClient: EmonClient,
                   eventBus: EventBus) {
    init {
        eventBus.register(this)
    }

    @Subscribe
    // TODO I think this will run on an EventBus thread
    fun sendSensorDataToEmon(sensor: Sensor) = runBlocking {
        logger.info("Sending ${sensor.name()} to EmonCMS")
        val time = measureTimeMillis {
            emonClient.sendUpdate(EmonUpdate(
                    sensor.group().reportingValue(),
                    mapOf(sensor.name() to sensor.stringValue())
            ))
        }
        logger.info("Sending data to EmonCMS took $time ms")
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.name)
    }
}
