package net.dinomite.gatherer.model

import kotlin.test.Test
import kotlin.test.assertEquals

internal class GroupTest {
    @Test
    fun fromString_Lowercase() {
        assertEquals(Group.DEVICE, Group.fromString("device"))
    }

    @Test
    fun fromString_MixedCase() {
        assertEquals(Group.DEVICE, Group.fromString("Device"))
    }

    @Test
    fun reportingValue() {
        assertEquals("environment", Group.ENVIRONMENT.reportingValue())
    }
}
