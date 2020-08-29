package net.dinomite.gatherer.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME
import org.apache.commons.collections4.queue.CircularFifoQueue
import java.time.Duration
import java.time.Instant

data class Sensor(
        @JsonProperty("group") val group: Group,
        @JsonProperty("name") val name: String,
        @JsonProperty("values") val values: CircularFifoQueue<Value<*>>
) {
    constructor(group: Group, name: String, value: Value<*>) :
            this(group, name, CircularFifoQueue<Value<*>>(10).apply { offer(value) })

    fun withinLast(duration: Duration): Boolean {
        if (values.isNotEmpty() && values.first().timestamp.isAfter(Instant.now().minus(duration))) {
            return true
        }
        return false
    }
}

@JsonTypeInfo(use = NAME, include = PROPERTY, property = "type")
@JsonSubTypes(
        JsonSubTypes.Type(IntValue::class, name = "IntValue"),
        JsonSubTypes.Type(DoubleValue::class, name = "DoubleValue")
)
abstract class Value<T> {
    @JsonProperty("timestamp")
    val timestamp: Instant = Instant.now()

    @JsonProperty("value")
    abstract fun value(): T
}

data class IntValue(private val value: Int) : Value<Int>() {
    override fun value() = value
}

data class DoubleValue(private val value: Double) : Value<Double>() {
    override fun value() = value
}
