package net.dinomite.gatherer.model

import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import net.dinomite.gatherer.model.Group.ENVIRONMENT
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class SensorTest {
    private val objectMapper = jacksonObjectMapper()
            .registerModule(JavaTimeModule())
            .configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true)
            .configure(SerializationFeature.INDENT_OUTPUT, true)

    private val intSensor = Sensor(ENVIRONMENT, "foo", IntValue(7))

    @Test
    fun roundtrip() {
        val json = objectMapper.writeValueAsString(intSensor)
        assertEquals(intSensor, objectMapper.readValue(json))
    }
}
