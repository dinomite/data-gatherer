package net.dinomite.producer.model

import com.fasterxml.jackson.annotation.JsonProperty
import net.dinomite.gatherer.model.Group.ENVIRONMENT
import net.dinomite.gatherer.model.Observation

/**
 * Raw data from `rtl_433 -F json` output
 */
data class RtlData(
        @JsonProperty("brand") val brand: String?,
        @JsonProperty("model") val model: String,
        @JsonProperty("id") val id: Int,
        @JsonProperty("battery_ok") val batteryOk: Int,
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
            sensorUpdates.add(SensorUpdate(ENVIRONMENT, sensorKey("temperature"), Observation(temperatureF)))
        }
        if (humidity != null) {
            sensorUpdates.add(SensorUpdate(ENVIRONMENT, sensorKey("humidity"), Observation(humidity)))
        }
//        sensorUpdates.add(SensorUpdate(BATTERY, sensorKey("battery"), Value(batteryOk == 1)))
        return sensorUpdates
    }
}
