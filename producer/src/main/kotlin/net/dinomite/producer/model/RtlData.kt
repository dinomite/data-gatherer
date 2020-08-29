package net.dinomite.producer.model

import com.fasterxml.jackson.annotation.JsonProperty
import net.dinomite.gatherer.model.DoubleValue
import net.dinomite.gatherer.model.Group.ENVIRONMENT
import net.dinomite.gatherer.model.IntValue

/**
 * Raw data from `rtl_433 -F json` output
 */
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

    fun toSensorUpdates(): List<SensorUpdate<*>> {
        val sensorUpdates = mutableListOf<SensorUpdate<*>>()
        if (temperatureC != null) {
            val temperatureF = temperatureC * 1.8 + 32
            sensorUpdates.add(SensorUpdate(ENVIRONMENT, sensorKey("temperature"), DoubleValue(temperatureF)))
        }
        if (humidity != null) {
            sensorUpdates.add(SensorUpdate(ENVIRONMENT, sensorKey("humidity"), IntValue(humidity)))
        }
        return sensorUpdates
    }
}
