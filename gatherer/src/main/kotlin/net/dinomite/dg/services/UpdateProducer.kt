package net.dinomite.dg.services

import net.dinomite.dp.model.Sensor

interface UpdateProducer {
    /**
     * Produce a list of Sensors
     */
    suspend fun sensorValues(): List<Sensor>
}
