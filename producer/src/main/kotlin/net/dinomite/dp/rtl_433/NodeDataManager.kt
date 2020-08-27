package net.dinomite.dp.rtl_433

import net.dinomite.dp.model.Sensor

class NodeDataManager {
    private val nodeData = mutableMapOf<String, Sensor>()

    fun updateNode(name: String, value: Sensor) { nodeData[name] = value }

    fun getValues() = nodeData.values.toList()
}
