package net.dinomite.gatherer.model

import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import net.dinomite.gatherer.model.Group.ENVIRONMENT
import java.time.Duration
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class SensorTest {
    private val objectMapper = jacksonObjectMapper()
        .registerModule(JavaTimeModule())
        .configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true)
        .configure(SerializationFeature.INDENT_OUTPUT, true)

    private val intSensor = Sensor(ENVIRONMENT, "foo", Observation(7))

    @Test
    fun equals() {
        val now = Instant.now()
        assertEquals(Sensor(ENVIRONMENT, "foo", Observation(7, now)), Sensor(ENVIRONMENT, "foo", Observation(7, now)))
    }

    @Test
    fun roundtrip() {
        val json = objectMapper.writeValueAsString(intSensor)
        assertEquals(intSensor, objectMapper.readValue(json))
    }

    @Test
    fun withinLast_NoObservations() {
        assertFalse {
            Sensor(ENVIRONMENT, "foo", EqualsCircularFifoQueue(10))
                .hasObservationWithinLast(Duration.ofSeconds(300))
        }
    }

    @Test
    fun withinLast_RecentObservation() {
        assertTrue { intSensor.hasObservationWithinLast(Duration.ofSeconds(10)) }
    }

    @Test
    fun withinLast_OldObservation() {
        assertFalse {
            Sensor(ENVIRONMENT, "foo", Observation(7, Instant.now().minusSeconds(500)))
                .hasObservationWithinLast(Duration.ofSeconds(300))
        }
    }
}
