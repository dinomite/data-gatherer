package net.dinomite.gatherer.hubitat

import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.readValue
import net.dinomite.gatherer.configuredObjectMapper
import net.dinomite.gatherer.hubitat.Device.Attribute
import net.dinomite.gatherer.hubitat.Device.Attribute.DataType.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class DeviceTest {
    private val objectMapper = configuredObjectMapper()
            .configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true)
            .configure(SerializationFeature.INDENT_OUTPUT, true)

    private val deviceJson = javaClass.getResource("/net/dinomite/gatherer/hubitat/device.json").readText()

    @Test
    fun deserialize() {
        val expected = Device(13, "Porch switch", listOf(
                Attribute("energy", "14.196", NUMBER),
                Attribute("firmware", null, STRING),
                Attribute("groups", null, NUMBER),
                Attribute("held", "1", NUMBER),
                Attribute("lastActivity", "2020 Aug 28 Fri 10:39:40 AM", STRING),
                Attribute("lastEvent", " Tap ▲", STRING),
                Attribute("numberOfButtons", "8", NUMBER),
                Attribute("power", "0", NUMBER),
                Attribute("pushed", "1", NUMBER),
                Attribute("switch", "off", ENUM)
        ))
        assertEquals(expected, objectMapper.readValue(deviceJson))
    }
}
