package net.dinomite.dp

import net.dinomite.dg.configuredObjectMapper
import net.dinomite.dp.model.DoubleSensor
import net.dinomite.dp.model.Group.ENVIRONMENT
import net.dinomite.dp.model.IntSensor
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class SensorsDeserializerTest {
    private val sensorsDeserializer = SensorsDeserializer(configuredObjectMapper())

    @Test
    fun deserialize() {
        val expected = listOf(
                DoubleSensor(ENVIRONMENT, "Acurite-Tower-12090-temperature", 73.75999999999999),
                IntSensor(ENVIRONMENT, "Acurite-Tower-12090-humidity", 54),
                DoubleSensor(ENVIRONMENT, "OS-Oregon-THN132N-25-temperature", 91.4)
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
