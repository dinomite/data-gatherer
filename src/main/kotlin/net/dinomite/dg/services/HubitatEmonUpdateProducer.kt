package net.dinomite.dg.services

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import net.dinomite.dg.emon.EmonUpdate
import net.dinomite.dg.hubitat.Device
import net.dinomite.dg.hubitat.HubitatClient
import org.slf4j.LoggerFactory

/**
 * Pulls information from Hubitat devices and packages it into EmonUpdates
 */
class HubitatEmonUpdateProducer(private val node: String,
                                private val devices: List<String>,
                                private val hubitatClient: HubitatClient) : EmonUpdateProducer {
    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.name)
    }

    /**
     * Retrieve power information for each device ID from Hubitat
     */
    override suspend fun buildUpdate(): EmonUpdate {
        val updates: Map<String, String> = retrieveDevices()
                .map { (deviceId, device) ->
                    if (device == null) {
                        deviceId to null
                    } else {
                        val power = device.attribute("power").intValue()

                        logger.debug("Power usage for ${device.name}: $power")
                        "${device.reportingName}_power" to power?.toString()
                    }
                }
                .toMap()
                .filter { (key, value) ->
                    if (value == null) {
                        logger.warn("Dropping update for $key because value is null")
                        false
                    } else {
                        true
                    }
                }
                .mapValues { it.value as String }

        return EmonUpdate(node, updates)
    }

    private suspend fun retrieveDevices(): Map<String, Device?> = withContext(Dispatchers.IO) {
        devices.map { deviceId ->
                    async { deviceId to hubitatClient.retrieveDevice(deviceId) }
                }
                .awaitAll()
                .toMap()
    }
}