package net.dinomite.dg.services

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import net.dinomite.dg.emon.EmonNode
import net.dinomite.dg.hubitat.Device
import net.dinomite.dg.hubitat.DeviceType
import net.dinomite.dg.hubitat.DeviceType.ENVIRONMENT
import net.dinomite.dg.hubitat.DeviceType.POWER
import net.dinomite.dg.hubitat.HubitatClient
import org.slf4j.LoggerFactory

/**
 * Pulls information from Hubitat devices and packages it into EmonUpdates
 */
class HubitatEmonUpdateProducer(private val devices: Map<String, DeviceType>,
                                private val hubitatClient: HubitatClient) : EmonUpdateProducer {
    /**
     * Retrieve information for each device ID from Hubitat
     */
    override suspend fun buildUpdates(): List<Map<EmonNode, Map<String, String>>> {
        val updates = mutableMapOf<EmonNode, Map<String, String>>()
        retrieveDevices()
                .mapNotNull { (deviceId, type, device) ->
                    when {
                        device == null -> {
                            logger.warn("Dropping update for $deviceId because it is null")
                            null
                        }
                        // TODO use device.capabilities to choose what to pull
                        type == POWER -> powerDevice(device)
                        type == ENVIRONMENT -> environmentDevice(device)
                        else -> {
                            logger.warn("Problem with $deviceId($type): $device")
                            null
                        }
                    }
                }
                .groupBy { (node, _) -> node }
                .map { (node, pairList) ->
                    node to pairList.map { it.second }
                }
                .forEach { (node, listOfMaps) ->
                    val map = mutableMapOf<String, String>()
                    listOfMaps.forEach { map.putAll(it) }
                    updates[node] = map
                }

        return listOf(updates)
    }

    private fun powerDevice(device: Device): Pair<EmonNode, Map<String, String>>? {
        val power = device.attribute("power").doubleValue()
        return if (power == null) {
            logger.warn("Power value for <${device.identity()}> is null")
            null
        } else {
            logger.debug("Power usage for ${device.identity()}: $power")
            EmonNode(POWER) to mapOf("${device.reportingName}_power" to power.toString())
        }
    }

    private fun environmentDevice(device: Device): Pair<EmonNode, Map<String, String>>? {
        val pressure = device.attribute("pressure").doubleValue()
        val temperature = device.attribute("temperature").doubleValue()
        val humidity = device.attribute("humidity").doubleValue()
        return if (pressure == null || temperature == null || humidity == null) {
            logger.warn("Incomplete data for <${device.identity()}> $pressure, $temperature, $humidity")
            null
        } else {
            logger.debug("Data for ${device.identity()}: $pressure, $temperature, $humidity")
            EmonNode(ENVIRONMENT) to mapOf(
                    "${device.reportingName}_pressure" to pressure.toString(),
                    "${device.reportingName}_temperature" to temperature.toString(),
                    "${device.reportingName}_humidity" to humidity.toString()
            )
        }
    }

    private suspend fun retrieveDevices(): List<Triple<String, DeviceType, Device?>> = withContext(Dispatchers.IO) {
        devices.map { (deviceId, type) -> async { Triple(deviceId, type, hubitatClient.retrieveDevice(deviceId)) } }
                .awaitAll()
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.name)
    }
}
