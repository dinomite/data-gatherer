package net.dinomtie.gatherer.dp

import net.dinomite.gatherer.dp.SensorsDeserializer
import net.dinomite.gatherer.model.Group.ENVIRONMENT
import net.dinomite.gatherer.model.Observation
import net.dinomite.gatherer.model.Sensor
import net.dinomtie.gatherer.testObjectMapper
import org.junit.jupiter.api.Test
import java.time.Instant
import kotlin.test.assertEquals

internal class SensorsDeserializerTest {
    private val sensorsDeserializer = SensorsDeserializer(testObjectMapper())

    @Test
    fun deserialize() {
        val timestamp = Instant.parse("2020-08-30T14:30:00Z")
        val expected = listOf(
                Sensor(ENVIRONMENT, "Acurite-Tower-12090-temperature", Observation(73.75, timestamp)),
                Sensor(ENVIRONMENT, "Acurite-Tower-12090-humidity", Observation(54, timestamp)),
                Sensor(ENVIRONMENT, "OS-Oregon-THN132N-25-temperature", Observation(91.4, timestamp))
        )

        val json = """[
                {
                    "group": "ENVIRONMENT",
                    "name": "Acurite-Tower-12090-temperature",
                    "observations": [
                        {
                            "value": 73.75,
                            "timestamp": "2020-08-30T14:30:00Z"
                        }
                    ]
                },
                {
                    "group": "ENVIRONMENT",
                    "name": "Acurite-Tower-12090-humidity",
                    "observations": [
                        {
                            "value": 54,
                            "timestamp": "2020-08-30T14:30:00Z"
                        }
                    ]
                },
                {
                    "group": "ENVIRONMENT",
                    "name": "OS-Oregon-THN132N-25-temperature",
                    "observations": [
                        {
                            "value": 91.4,
                            "timestamp": "2020-08-30T14:30:00Z"
                        }
                    ]
                }
        ]"""

        assertEquals(expected, sensorsDeserializer.deserialize(json))
    }
}
