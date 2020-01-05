package net.dinomite.dg.services

import com.google.common.util.concurrent.AbstractScheduledService
import kotlinx.coroutines.runBlocking
import net.dinomite.dg.DataGathererConfig
import net.dinomite.dg.emon.EmonClient
import net.dinomite.dg.emon.EmonUpdate
import net.dinomite.dg.hubitat.HubitatClient
import org.slf4j.LoggerFactory
import java.time.Duration.ZERO

@Suppress("UnstableApiUsage")
class HubitatScheduleService(private val config: DataGathererConfig,
                             private val hubitatClient: HubitatClient,
                             private val emonClient: EmonClient) : AbstractScheduledService() {
    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.name)
    }

    private val devices = config.HUBITAT_DEVICES

    override fun scheduler(): Scheduler = Scheduler.newFixedRateSchedule(ZERO, config.SCHEDULED_SERVICE_INTERVAL)

    override fun runOneIteration() = runBlocking {
        val updates: Map<String, String> = devices
                .map { deviceId ->
                    val device = hubitatClient.retrieveDevice(deviceId)
                    val power = device.attributes.first { it.name == "power" }.intValue()

                    logger.debug("Power usage for ${device.name}: $power")
                    "${device.reportingName}_power" to power?.toString()
                }
                .toMap()
                .filter { (key, value) ->
                    if (value == null) {
                        logger.warn("Dropping update for $key because value is null")
                    }
                    value != null
                }
                .mapValues { it.value as String }

        emonClient.sendUpdate(EmonUpdate(config.EMON_NODE, updates))
    }
}
