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
 * Pulls power information from Hubitat devices and packages it into EmonUpdates
 */
class HubitatPowerEmonUpdateProducer(private val node: String,
                                     private val devices: List<String>,
                                     private val hubitatClient: HubitatClient) : EmonUpdateProducer {
    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.name)
    }

    /**
     * Retrieve power information for each device ID from Hubitat
     */
    override suspend fun buildUpdate(): EmonUpdate {
        val updates = retrieveDevices()
                .mapNotNull { (deviceId, device) ->
                    if (device == null) {
                        logger.warn("Dropping update for $deviceId because it is null")
                        null
                    } else {
                        val power = device.attribute("power").intValue()
                        if (power == null) {
                            logger.warn("Power value for <${device.identity()}> is null")
                            null
                        } else {
                            logger.debug("Power usage for ${device.identity()}: $power")
                            "${device.reportingName}_power" to power.toString()
                        }
                    }
                }
                .toMap()

        return EmonUpdate(node, updates)
    }

    private suspend fun retrieveDevices(): Map<String, Device?> = withContext(Dispatchers.IO) {
        devices.map { deviceId -> async { deviceId to hubitatClient.retrieveDevice(deviceId) } }
                .awaitAll()
                .toMap()
    }
}