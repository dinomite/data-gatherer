package net.dinomite.gatherer.hubitat

import com.fasterxml.jackson.module.kotlin.readValue
import net.dinomite.gatherer.hubitat.HubitatDevice.Attribute
import net.dinomite.gatherer.hubitat.HubitatDevice.Attribute.DataType.*
import net.dinomite.gatherer.testObjectMapper
import kotlin.test.Test
import kotlin.test.assertEquals

internal class HubitatDeviceTest {
    private val deviceJson = javaClass.getResource("/net/dinomite/gatherer/hubitat/device.json").readText()

    @Test
    fun deserialize() {
        val expected = HubitatDevice(13, "Porch switch", listOf(
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
        assertEquals(expected, testObjectMapper().readValue(deviceJson))
    }
}