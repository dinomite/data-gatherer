package net.dinomite.producer.model

import net.dinomite.gatherer.model.Group
import net.dinomite.gatherer.model.Observation
import net.dinomite.gatherer.model.Sensor

class SensorUpdate<out T : Number>(
        private val group: Group,
        val name: String,
        val observation: Observation<T>
) {
    fun toSensor() = Sensor(group, name, observation)
}
