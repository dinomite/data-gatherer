package net.dinomite.gatherer.awair

import com.fasterxml.jackson.module.kotlin.readValue
import net.dinomite.gatherer.testObjectMapper
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals

internal class AwairDeviceTest {
    private val objectMapper = testObjectMapper()

    @Test
    fun awairSensor_Deserialize() {
        val awairSensor = AwairSensor(AwairSensor.Comp.CO2, 500.0)

        val json = """{"comp": "co2", "value": 500}"""

        assertEquals(awairSensor, objectMapper.readValue(json))
    }

    @Test
    fun awairData_Deserialize() {
        val awairData = AwairData(
            Instant.parse("2021-01-25T09:15:00Z"),
            95,
            listOf(AwairSensor(AwairSensor.Comp.CO2, 500.0))
        )

        val json = """{
            "timestamp": "2021-01-25T09:15:00Z",
            "score": 95,
            "sensors": [{"comp": "co2", "value": 500}]
        }""".trimIndent()

        assertEquals(awairData, objectMapper.readValue(json))
    }

    @Test
    fun awairDevice_Deserialize() {
        val awairDevice = AwairDevice(
            listOf(
                AwairData(
                    Instant.parse("2021-01-25T09:15:00Z"),
                    95,
                    listOf(AwairSensor(AwairSensor.Comp.CO2, 500.0))
                )
            )
        )

        val json = """{
            "data": [
                {
                    "timestamp": "2021-01-25T09:15:00Z",
                    "score": 95,
                    "sensors": [{"comp": "co2", "value": 500}]
                }
            ]
        }""".trimIndent()

        assertEquals(awairDevice, objectMapper.readValue(json))
    }
}
