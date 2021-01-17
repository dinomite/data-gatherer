package net.dinomite.gatherer.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Duration
import java.time.Instant

data class Sensor(
        @JsonProperty("group") val group: Group,
        @JsonProperty("name") val name: String,
        @JsonProperty("observations") val observations: EqualsCircularFifoQueue<Observation<*>>
) {
    constructor(group: Group, name: String, observation: Observation<*>) :
            this(group, name, EqualsCircularFifoQueue<Observation<*>>(10).apply { offer(observation) })

    fun withinLast(duration: Duration): Boolean {
        return observations.isNotEmpty() && observations.first().timestamp.isAfter(Instant.now().minus(duration))
    }
}

data class Observation<out T : Number>(
        @JsonProperty("value") val value: T,
        @JsonProperty("timestamp") val timestamp: Instant = Instant.now()
)
