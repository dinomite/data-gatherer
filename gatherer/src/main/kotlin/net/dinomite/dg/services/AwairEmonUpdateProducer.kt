package net.dinomite.dg.services

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import net.dinomite.dg.awair.AwairClient
import net.dinomite.dg.awair.Device
import net.dinomite.dg.awair.Sensor
import net.dinomite.dg.emon.EmonNode
import org.slf4j.LoggerFactory

/**
 * Pulls environment information from Awair and packages into an EmonUpdate
 */
class AwairEmonUpdateProducer(private val node: EmonNode,
                              private val devices: List<String>,
                              private val awairClient: AwairClient) : EmonUpdateProducer {
    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.name)
    }

    /**
     * Retrieve environment information from each Awair device
     */
    override suspend fun buildUpdates(): List<Map<EmonNode, Map<String, String>>> {
        val updates = retrieveDevices()
                .map { (deviceId, device) ->
                    if (device == null) {
                        logger.warn("Dropping update for $deviceId because it is null")
                        null
                    } else {
                        Sensor.Comp.values().map { comp ->
                            val value = device.sensorValue(comp)
                            if (value == null) {
                                logger.warn("${comp.name} value for <$deviceId> is null")
                                null
                            } else {
                                logger.debug("${comp.name} for $deviceId: $value")
                                "awair_${deviceId}_${comp.name.toLowerCase()}" to value.toString()
                            }
                        }
                    }
                }
                .filterNotNull()
                .flatten()
                .filterNotNull()
                .toMap()
        return listOf(mapOf(node to updates))
    }

    private suspend fun retrieveDevices(): Map<String, Device?> = withContext(Dispatchers.IO) {
        devices.map { deviceId -> async { deviceId to awairClient.retrieveDevice(deviceId) } }
                .awaitAll()
                .toMap()
    }
}
