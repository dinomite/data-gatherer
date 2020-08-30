package net.dinomite.gatherer.hubitat

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import net.dinomite.gatherer.hubitat.DeviceType.POWER
import net.dinomite.gatherer.model.Group.ENERGY
import net.dinomite.gatherer.model.Observation
import net.dinomite.gatherer.model.Sensor
import net.dinomite.gatherer.services.UpdateProducer
import org.slf4j.LoggerFactory

/**
 * Pulls information from Hubitat devices and packages it into EmonUpdates
 */
class HubitatUpdateProducer(private val devices: Map<String, DeviceType>,
                            private val hubitatClient: HubitatClient) : UpdateProducer {
    /**
     * Retrieve information for each device ID from Hubitat
     */
    override suspend fun sensors(): List<Sensor> {
        val retrieveDevices = retrieveDevices()
        return retrieveDevices
                .mapNotNull { (deviceId, type, device) ->
                    when {
                        device == null -> {
                            logger.warn("Dropping update for $deviceId because it is null")
                            null
                        }
                        // TODO use device.capabilities to choose what to pull
                        type == POWER -> powerDevice(device)
                        else -> {
                            logger.warn("Problem with $deviceId($type): $device")
                            null
                        }
                    }
                }
    }

    private fun powerDevice(device: Device): Sensor? {
        val power = device.attribute("power").Value()
        return if (power == null) {
            logger.warn("Power value for <${device.identity()}> is null")
            null
        } else {
            logger.debug("Power usage for ${device.identity()}: $power")
            Sensor(ENERGY, "${device.reportingName}_power", Observation(power))
        }
    }

    private suspend fun retrieveDevices(): List<Triple<String, DeviceType, Device?>> = withContext(Dispatchers.IO) {
        devices.map { (deviceId, type) -> async { Triple(deviceId, type, hubitatClient.retrieveDevice(deviceId)) } }
                .awaitAll()
    }

    companion object {
        private val logger = LoggerFactory.getLogger(HubitatUpdateProducer::class.java.name)
    }
}
