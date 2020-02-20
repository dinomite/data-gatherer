package net.dinomite.dg.awair

import com.fasterxml.jackson.annotation.JsonCreator
import java.time.Instant

data class Device(val data: List<AwairData>) {
    fun sensorValue(comp: Sensor.Comp): Double? = data.first().sensors.firstOrNull { it.comp == comp }?.value
}

data class AwairData(val timestamp: Instant, val score: Int, val sensors: List<Sensor>)

data class Sensor(val comp: Comp, val value: Double) {
    enum class Comp(val value: String) {
        TEMPERATURE("temp"),
        HUMIDITY("humid"),
        CO2("co2"),
        VOC("voc"),
        PM25("pm25");

        companion object {
            @JvmStatic
            @JsonCreator
            fun fromString(input: String) = values().firstOrNull { it.value == input }
        }
    }
}