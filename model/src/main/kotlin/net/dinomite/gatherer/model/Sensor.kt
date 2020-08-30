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
}

data class Observation<T>(
        @JsonProperty("value") val value: T,
        @JsonProperty("timestamp") val timestamp: Instant = Instant.now()
)
