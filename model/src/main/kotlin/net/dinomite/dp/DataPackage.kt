package net.dinomite.dp

import com.fasterxml.jackson.annotation.*
import com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME

data class NodeData @JsonCreator(mode = JsonCreator.Mode.DELEGATING) constructor(@JsonValue val sensors: List<Sensor>)

@JsonTypeInfo(use = NAME, include = PROPERTY, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(IntSensor::class, name = "IntSensor"),
    JsonSubTypes.Type(DoubleSensor::class, name = "DoubleSensor")
)
abstract class Sensor {
    @JsonProperty("name")
    abstract fun name(): String

    @JsonProperty("value")
    abstract fun stringValue(): String
}

data class IntSensor(val name: String, private val value: Int): Sensor() {
    override fun name(): String = name

    override fun stringValue(): String = "$value"

}

data class DoubleSensor(val name: String, private val value: Double): Sensor() {
    override fun name(): String = name

    override fun stringValue(): String = "$value"
}
