package net.dinomite.gatherer.services

import net.dinomite.gatherer.model.Sensor

interface UpdateProducer {
    /**
     * Produce a list of Sensors
     */
    suspend fun sensors(): List<Sensor>
}
