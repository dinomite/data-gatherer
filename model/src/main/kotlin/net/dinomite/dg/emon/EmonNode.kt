package net.dinomite.dg.emon

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

data class EmonNode @JsonCreator(mode = JsonCreator.Mode.DELEGATING) constructor(@JsonValue val value: String)
