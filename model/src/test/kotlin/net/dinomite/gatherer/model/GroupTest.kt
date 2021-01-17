package net.dinomite.gatherer.model

import org.junit.jupiter.api.Test
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
}
