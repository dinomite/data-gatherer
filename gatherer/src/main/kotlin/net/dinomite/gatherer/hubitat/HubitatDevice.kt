package net.dinomite.gatherer.hubitat

import net.dinomite.gatherer.hubitat.HubitatDevice.Attribute.DataType.NUMBER
import net.dinomite.gatherer.model.Group

data class HubitatDevice(val id: Int, val name: String, val attributes: List<Attribute>) {
    val reportingName by lazy { name.replace(' ', '_').toLowerCase() }

    fun attribute(name: String): Attribute = attributes.first { it.name == name }

    fun identity(): String = "$id—$name"

    data class Attribute(
        val name: String,
        val currentValue: String?,
        val dataType: DataType
    ) {
        enum class DataType {
            NUMBER,
            STRING,
            ENUM,
            DATE
        }

        fun value(): Double? {
            // Only deal in numeric values for now
            if (dataType != NUMBER) {
                throw IllegalAccessError("DataType is $dataType, not $NUMBER")
            }

            return currentValue?.toDouble()
        }
    }
}

enum class HubitatDeviceType(private val node: String) {
    POWER("energy"),
    ENVIRONMENT("environment");

    fun group() = Group.fromString(this.node)
}
