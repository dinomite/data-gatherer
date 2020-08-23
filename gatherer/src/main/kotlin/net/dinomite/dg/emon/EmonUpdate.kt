package net.dinomite.dg.emon

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonCreator.Mode.DELEGATING
import com.fasterxml.jackson.annotation.JsonValue
import net.dinomite.dg.hubitat.DeviceType

data class EmonUpdate(val node: EmonNode, val updates: Map<String, String>)

data class EmonNode @JsonCreator(mode = DELEGATING) constructor(@JsonValue val value: String) {
    constructor(type: DeviceType) : this(type.node)
}
