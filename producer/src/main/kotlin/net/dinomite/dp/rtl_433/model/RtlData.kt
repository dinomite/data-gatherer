package net.dinomite.dp.rtl_433.model

import com.fasterxml.jackson.annotation.JsonProperty
import net.dinomite.dp.DoubleSensor
import net.dinomite.dp.IntSensor
import net.dinomite.dp.Sensor

data class RtlData(
        @JsonProperty("brand") val brand: String?,
        @JsonProperty("model") val model: String,
        @JsonProperty("id") val id: Int,
        @JsonProperty("battery_ok") val batteryOk: Int,
        // TODO convert to fahrenheit
        @JsonProperty("temperature_C") val temperatureC: Double?,
        @JsonProperty("humidity") val humidity: Int?
) {
    private fun sensorKey(subSensor: String) = if (brand != null) {
        "$brand-$model-$id-$subSensor"
    } else {
        "$model-$id-$subSensor"
    }

    fun toSensors(): List<Sensor> {
        val sensors = mutableListOf<Sensor>()
        if (temperatureC != null) {
            sensors.add(DoubleSensor(sensorKey("temperature"), temperatureC))
        }
        if (humidity != null) {
            sensors.add(IntSensor(sensorKey("humidity"), humidity))
        }
        return sensors
    }
}
