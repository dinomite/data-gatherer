package net.dinomite.gatherer.emon

import com.google.common.eventbus.Subscribe
import com.google.inject.Inject
import kotlinx.coroutines.runBlocking
import net.dinomite.gatherer.model.Sensor
import org.slf4j.LoggerFactory
import kotlin.system.measureTimeMillis

@Suppress("UnstableApiUsage")
class EmonReporter
@Inject constructor(private val emonClient: EmonClient) {
    @Subscribe
    fun sendSensorDataToEmon(sensor: Sensor) = runBlocking {
        logger.info("Sending ${sensor.name()} to EmonCMS")
        val time = measureTimeMillis {
            emonClient.sendUpdate(EmonUpdate(
                    sensor.group().reportingValue(),
                    mapOf(sensor.name() to sensor.stringValue())
            ))
        }
        logger.debug("Sending data to EmonCMS took $time ms")
    }

    companion object {
        private val logger = LoggerFactory.getLogger(EmonReporter::class.java.name)
    }
}
