package net.dinomite.dp

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

data class NodeData @JsonCreator(mode = JsonCreator.Mode.DELEGATING) constructor(@JsonValue val sensors: List<Sensor>)
