package net.dinomite.dg.awair

import com.fasterxml.jackson.annotation.JsonCreator
import net.dinomite.dg.awair.Sensor.Comp.TEMPERATURE
import java.time.Instant

/**
{
    "data": [
        {
            "timestamp": "2020-02-20T14:50:02.255Z",
            "score": 98,
            "sensors": [
                {
                    "comp": "temp",
                    "value": 22.020000457763672
                },
                {
                    "comp": "humid",
                    "value": 41.95000076293945
                },
                {
                    "comp": "co2",
                    "value": 573
                },
                {
                    "comp": "voc",
                    "value": 33
                },
                {
                    "comp": "pm25",
                    "value": 1
                }
            ]
        }
    ]
}
*/
data class Device(val data: List<AwairData>) {
    fun temperature() = data.first().sensors.firstOrNull { it.comp == TEMPERATURE }?.value
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