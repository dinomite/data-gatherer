package net.dinomite.dg.services

import net.dinomite.dg.emon.EmonUpdate
import net.dinomite.dg.hubitat.HubitatClient
import org.slf4j.LoggerFactory

class HubitatEmonUpdater(private val node: String,
                         private val devices: List<String>,
                         private val hubitatClient: HubitatClient) : EmonUpdateProducer {
    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.name)
    }

    /**
     * Retrieve power information for each device ID from Hubitat
     */
    override suspend fun buildUpdate(): EmonUpdate {
        val updates: Map<String, String> = devices
                .map { deviceId ->
                    val device = hubitatClient.retrieveDevice(deviceId)
                    if (device == null) {
                        null to null
                    } else {
                        val power = device.attribute("power").intValue()

                        logger.debug("Power usage for ${device.name}: $power")
                        "${device.reportingName}_power" to power?.toString()
                    }
                }
                .toMap()
                .filter { (key, value) ->
                    if (key == null || value == null) {
                        logger.warn("Dropping update for $key because value is null")
                    }
                    value != null
                }
                .mapKeys { it.key as String }
                .mapValues { it.value as String }

        return EmonUpdate(node, updates)
    }
}