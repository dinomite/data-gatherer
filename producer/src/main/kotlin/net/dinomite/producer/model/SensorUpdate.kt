package net.dinomite.producer.model

import net.dinomite.gatherer.model.Group
import net.dinomite.gatherer.model.Sensor
import net.dinomite.gatherer.model.Value

class SensorUpdate<T>(
        private val group: Group,
        val name: String,
        val value: Value<T>
) {
    fun toSensor() = Sensor(group, name, value)
}
