package net.dinomite.gatherer.model

import com.fasterxml.jackson.annotation.JsonProperty
import org.apache.commons.collections4.queue.CircularFifoQueue
import java.time.Duration
import java.time.Instant

data class Sensor(
        @JsonProperty("group") val group: Group,
        @JsonProperty("name") val name: String,
        @JsonProperty("observations") val observations: CircularFifoQueue<Observation<*>>
) {
    constructor(group: Group, name: String, observation: Observation<*>) :
            this(group, name, CircularFifoQueue<Observation<*>>(10).apply { offer(observation) })

    fun withinLast(duration: Duration): Boolean {
        if (observations.isNotEmpty() && observations.first().timestamp.isAfter(Instant.now().minus(duration))) {
            return true
        }
        return false
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Sensor

        if (group != other.group) return false
        if (name != other.name) return false
        if (observations.size != other.observations.size) return false
        observations.forEachIndexed { index, observation ->
            if (observation != other.observations[index]) return false
        }

        return true
    }

    override fun hashCode(): Int {
        var result = group.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + observations.hashCode()
        return result
    }
}

data class Observation<out T : Number>(
        @JsonProperty("value") val value: T,
        @JsonProperty("timestamp") val timestamp: Instant = Instant.now()
)
