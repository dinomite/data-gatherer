package net.dinomite.dg.hubitat

import net.dinomite.dg.emon.EmonNode
import net.dinomite.dg.hubitat.Device.Attribute.DataType.NUMBER

data class Device(val id: Int, val name: String, val attributes: List<Attribute>) {
    val reportingName by lazy { name.replace(' ', '_').toLowerCase() }

    fun attribute(name: String) = attributes.first { it.name == name }

    fun identity() = "$id—$name"

    data class Attribute(val name: String,
                         val currentValue: String?,
                         val dataType: DataType) {
        enum class DataType {
            NUMBER,
            STRING,
            ENUM,
            DATE
        }

        fun doubleValue(): Double? {
            if (dataType != NUMBER) {
                throw IllegalAccessError("DataType is $dataType, not $NUMBER")
            }

            return currentValue?.toDouble()
        }
    }
}

enum class DeviceType(val node: String) {
    POWER("energy"),
    ENVIRONMENT("environment");

    fun emonNode() = EmonNode(this.node)
}
