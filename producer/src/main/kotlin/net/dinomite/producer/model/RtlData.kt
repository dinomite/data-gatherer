package net.dinomite.producer.model

import com.fasterxml.jackson.annotation.JsonProperty
import net.dinomite.gatherer.model.Group
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
    fun sensorName(): String = if (brand != null) {
        "$brand-$model-$id"
    } else {
        "$model-$id"
    }

    private fun sensorKey(subSensor: String) = "${sensorName()}-$subSensor"

    fun toSensorUpdates(): List<SensorUpdate<*>> {
        return mutableListOf<SensorUpdate<*>>().apply {
            temperatureC?.let {
                val temperatureF = temperatureC * 1.8 + 32
                add(SensorUpdate(ENVIRONMENT, sensorKey("temperature"), Observation(temperatureF)))
            }
            humidity?.let {
                add(SensorUpdate(ENVIRONMENT, sensorKey("humidity"), Observation(humidity)))
            }
            add(SensorUpdate(Group.BATTERY, sensorKey("battery"), Observation(batteryOk)))
        }.toList()
    }
}
