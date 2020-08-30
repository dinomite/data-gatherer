package net.dinomite.producer

import net.dinomite.gatherer.model.Sensor
import net.dinomite.producer.model.SensorUpdate

class NodeDataManager {
    private val nodeData = mutableMapOf<String, Sensor>()

    fun updateNode(sensorUpdate: SensorUpdate<*>) {
        val sensor = nodeData[sensorUpdate.name]
        if (sensor == null) {
            nodeData[sensorUpdate.name] = sensorUpdate.toSensor()
        }  else {
            nodeData[sensorUpdate.name] = sensor.apply { observations.offer(sensorUpdate.observation) }
        }
    }

    fun getValues() = nodeData.values.toList()
}
