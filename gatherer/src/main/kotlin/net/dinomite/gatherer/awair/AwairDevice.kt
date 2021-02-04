package net.dinomite.gatherer.awair

import com.fasterxml.jackson.annotation.JsonCreator
import java.time.Instant

data class AwairDevice(val data: List<AwairData>) {
    fun sensorValue(comp: AwairSensor.Comp): Double? {
        return data.firstOrNull()
            ?.sensors
            ?.firstOrNull { it.comp == comp }
            ?.value
    }

    fun timestamp(): Instant? = data.firstOrNull()?.timestamp
}

data class AwairData(val timestamp: Instant, val score: Int, val sensors: List<AwairSensor>)

data class AwairSensor(val comp: Comp, val value: Double) {
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
