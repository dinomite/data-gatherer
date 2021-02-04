package net.dinomite.gatherer.awair

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import net.dinomite.gatherer.model.Group.ENVIRONMENT
import net.dinomite.gatherer.model.Observation
import net.dinomite.gatherer.model.Sensor
import net.dinomite.gatherer.services.UpdateProducer
import org.slf4j.LoggerFactory
import java.time.Instant

/**
 * Pulls environment information from Awair and packages into an EmonUpdate
 */
class AwairUpdateProducer(private val devices: List<String>,
                          private val awairClient: AwairClient) : UpdateProducer {
    /**
     * Retrieve environment information from each Awair device
     */
    override suspend fun sensors(): List<Sensor> {
        return retrieveDevices()
                .map { (deviceId, device) ->
                    if (device == null) {
                        logger.warn("Dropping update for $deviceId because it is null")
                        null
                    } else {
                        AwairSensor.Comp.values().map { comp ->
                            val value = device.sensorValue(comp)
                            if (value == null) {
                                logger.warn("${comp.name} value for <$deviceId> is null")
                                null
                            } else {
                                logger.debug("${comp.name} for $deviceId: $value")
                                Sensor(
                                        ENVIRONMENT,
                                        "awair_${deviceId}_${comp.name.toLowerCase()}",
                                        Observation(value, device.timestamp() ?: Instant.now())
                                )
                            }
                        }
                    }
                }
                .filterNotNull()
                .flatten()
                .filterNotNull()
    }

    private suspend fun retrieveDevices(): Map<String, AwairDevice?> = withContext(Dispatchers.IO) {
        devices.map { deviceId -> async { deviceId to awairClient.retrieveDevice(deviceId) } }
                .awaitAll()
                .toMap()
    }

    companion object {
        private val logger = LoggerFactory.getLogger(AwairUpdateProducer::class.java.name)
    }
}
