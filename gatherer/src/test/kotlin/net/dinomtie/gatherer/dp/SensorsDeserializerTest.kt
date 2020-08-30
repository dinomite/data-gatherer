package net.dinomtie.gatherer.dp

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import net.dinomite.gatherer.dp.SensorsDeserializer
import net.dinomite.gatherer.model.Group.ENVIRONMENT
import net.dinomite.gatherer.model.Observation
import net.dinomite.gatherer.model.Sensor
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class SensorsDeserializerTest {
    private val sensorsDeserializer = SensorsDeserializer(jacksonObjectMapper().apply {
        registerModule(JavaTimeModule())
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    })

    @Test
    fun deserialize() {
        val expected = listOf(
                Sensor(ENVIRONMENT, "Acurite-Tower-12090-temperature", Observation(73.75999999999999)),
                Sensor(ENVIRONMENT, "Acurite-Tower-12090-humidity", Observation(54)),
                Sensor(ENVIRONMENT, "OS-Oregon-THN132N-25-temperature", Observation(91.4))
        )

        val json = """[
                {
                    "group": "ENVIRONMENT",
                    "name": "Acurite-Tower-12090-temperature",
                    "type": "DoubleSensor",
                    "value": "73.75999999999999"
                },
                {
                    "group": "ENVIRONMENT",
                    "name": "Acurite-Tower-12090-humidity",
                    "type": "IntSensor",
                    "value": "54"
                },
                {
                    "group": "ENVIRONMENT",
                    "name": "OS-Oregon-THN132N-25-temperature",
                    "type": "DoubleSensor",
                    "value": "91.4"
                }
        ]"""

        assertEquals(expected, sensorsDeserializer.deserialize(json))
    }
}
