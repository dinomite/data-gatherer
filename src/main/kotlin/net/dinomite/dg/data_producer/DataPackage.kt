package net.dinomite.dg.data_producer

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME
import com.fasterxml.jackson.annotation.JsonValue

data class NodeData @JsonCreator(mode = JsonCreator.Mode.DELEGATING) constructor(@JsonValue val sensors: List<Sensor>)

@JsonTypeInfo(use = NAME, include = PROPERTY, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(IntSensor::class, name = "IntSensor"),
    JsonSubTypes.Type(DoubleSensor::class, name = "DoubleSensor")
)
interface Sensor {
    fun stringValue(): String
    fun name(): String
}

data class IntSensor(val name: String, private val value: Int): Sensor {
    override fun name(): String = name

    override fun stringValue(): String = "$value"

}

data class DoubleSensor(val name: String, private val value: Double): Sensor {
    override fun name(): String = name

    override fun stringValue(): String = "$value"
}
