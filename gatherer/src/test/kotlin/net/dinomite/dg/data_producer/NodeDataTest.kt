package net.dinomite.dg.data_producer

import net.dinomite.dg.configuredObjectMapper
import net.dinomite.dg.emon.EmonNode
import net.dinomite.dp.DoubleSensor
import net.dinomite.dp.IntSensor
import net.dinomite.dp.NodeData
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class NodeDataTest {
    private val objectMapper = configuredObjectMapper()

    @Test
    fun deserialize() {
        val expected = mapOf(
                EmonNode("environment") to NodeData(listOf(IntSensor("attic_temp", 94))),
                EmonNode("devices") to NodeData(listOf(DoubleSensor("rpi_zero_cpu_temp", 56.2)))
        )

        val json = """{
                "environment": [
                    {
                      "name": "attic_temp",
                      "type": "IntSensor",
                      "value": 94
                    }
                ],
                "devices": [
                    {
                      "name": "rpi_zero_cpu_temp",
                      "type": "DoubleSensor",
                      "value": 56.2
                    }
                ]
            }"""

        assertEquals(expected, objectMapper.readValue(json, SensorsDeserializer.typeRef))
    }
}
