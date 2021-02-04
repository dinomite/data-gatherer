package net.dinomite.gatherer.hubitat

import com.fasterxml.jackson.module.kotlin.readValue
import net.dinomite.gatherer.hubitat.HubitatDevice.Attribute
import net.dinomite.gatherer.hubitat.HubitatDevice.Attribute.DataType.ENUM
import net.dinomite.gatherer.hubitat.HubitatDevice.Attribute.DataType.NUMBER
import net.dinomite.gatherer.hubitat.HubitatDevice.Attribute.DataType.STRING
import net.dinomite.gatherer.model.Group
import net.dinomite.gatherer.testObjectMapper
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals

internal class HubitatDeviceTest {
    private val deviceJson = javaClass.getResource("/net/dinomite/gatherer/hubitat/device.json").readText()

    @Test
    fun deserialize() {
        val expected = HubitatDevice(
            13, "Porch switch", listOf(
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
            )
        )
        assertEquals(expected, testObjectMapper().readValue(deviceJson))
    }

    @Test
    fun findsAttributeByName() {
        val expected = Attribute("pushed", "1", NUMBER)
        val device = HubitatDevice(13, "Porch switch", listOf(expected))

        assertEquals(expected, device.attribute(expected.name))
    }

    @Test
    fun reportingName() {
        val device = HubitatDevice(13, "Porch switch", listOf(Attribute("pushed", "1", NUMBER)))

        assertEquals("porch_switch", device.reportingName)
    }

    @Test
    fun attribute_ValueConvertsToDouble() {
        val device = HubitatDevice(13, "Porch switch", listOf(Attribute("pushed", "1", NUMBER)))

        assertEquals(1.0, device.attributes.first().value())
    }

    @Test
    fun attribute_ValueStringNotSupported() {
        val device = HubitatDevice(13, "Porch switch", listOf(Attribute("pushed", "foo", STRING)))

        assertThrows<IllegalAccessError> {
            device.attributes.first().value()
        }
    }

    @Test
    fun deviceType_ToGroup() {
        assertEquals(Group.ENVIRONMENT, HubitatDeviceType.ENVIRONMENT.group())
    }
}
