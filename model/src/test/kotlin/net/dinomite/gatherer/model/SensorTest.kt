package net.dinomite.gatherer.model

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import net.dinomite.gatherer.model.Group.ENVIRONMENT
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class SensorTest {
    private val objectMapper = jacksonObjectMapper()
            .configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true)
            .configure(SerializationFeature.INDENT_OUTPUT, true)

    private val intSensor = IntSensor(ENVIRONMENT, "foo", 7)
    private val json = """
        {
          "type" : "IntSensor",
          "name" : "foo",
          "value" : "7"
        }
    """.trimIndent()

    @Test
    fun serialize() {
        assertEquals(json, objectMapper.writeValueAsString(intSensor))
    }

    @Test
    fun deserialize() {
        assertEquals(intSensor, objectMapper.readValue(json))
    }

    @Test
    fun foo() {
        val sensors = listOf(IntSensor(ENVIRONMENT, "foo", 7), DoubleSensor(ENVIRONMENT, "bar", 9.0))
        println(objectMapper.writerFor(object : TypeReference<List<Sensor>>() {}).writeValueAsString(sensors))
    }
}
