package net.dinomite.producer.model

import com.fasterxml.jackson.annotation.JsonProperty
import net.dinomite.gatherer.model.DoubleSensor
import net.dinomite.gatherer.model.Group.ENVIRONMENT
import net.dinomite.gatherer.model.IntSensor
import net.dinomite.gatherer.model.Sensor

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
            val temperatureF = temperatureC * 1.8 + 32
            sensors.add(DoubleSensor(ENVIRONMENT, sensorKey("temperature"), temperatureF))
        }
        if (humidity != null) {
            sensors.add(IntSensor(ENVIRONMENT, sensorKey("humidity"), humidity))
        }
        return sensors
    }
}
