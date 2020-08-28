package net.dinomite.gatherer.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME

@JsonTypeInfo(use = NAME, include = PROPERTY, property = "type")
@JsonSubTypes(
        JsonSubTypes.Type(IntSensor::class, name = "IntSensor"),
        JsonSubTypes.Type(DoubleSensor::class, name = "DoubleSensor")
)
abstract class Sensor {
    @JsonProperty("group")
    abstract fun group(): Group

    @JsonProperty("name")
    abstract fun name(): String

    @JsonProperty("value")
    abstract fun stringValue(): String
}

data class IntSensor(
        val group: Group,
        val name: String,
        private val value: Int
) : Sensor() {
    override fun group(): Group = group
    override fun name(): String = name
    override fun stringValue(): String = "$value"
}

data class DoubleSensor(
        val group: Group,
        val name: String,
        private val value: Double
) : Sensor() {
    override fun group(): Group = group
    override fun name(): String = name
    override fun stringValue(): String = "$value"
}
