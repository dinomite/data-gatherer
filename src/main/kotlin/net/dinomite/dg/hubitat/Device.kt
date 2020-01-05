package net.dinomite.dg.hubitat

import net.dinomite.dg.hubitat.Device.Attribute.DataType.NUMBER

data class Device(val id: Int, val name: String, val attributes: List<Attribute>) {
    val reportingName by lazy { name.replace(' ', '_').toLowerCase() }

    data class Attribute(val name: String,
                         val currentValue: String?,
                         val dataType: DataType) {
        enum class DataType(val value: String) {
            NUMBER("NUMBER"),
            STRING("STRING"),
            ENUM("ENUM")
        }

        fun intValue(): Int? {
            if (dataType != NUMBER) {
                throw IllegalAccessError("DataType is $dataType, not $NUMBER")
            }

            return currentValue?.toInt()
        }
    }
}