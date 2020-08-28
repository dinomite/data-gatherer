package net.dinomite.producer

import net.dinomite.gatherer.model.Sensor

class NodeDataManager {
    private val nodeData = mutableMapOf<String, Sensor>()

    fun updateNode(name: String, value: Sensor) { nodeData[name] = value }

    fun getValues() = nodeData.values.toList()
}
