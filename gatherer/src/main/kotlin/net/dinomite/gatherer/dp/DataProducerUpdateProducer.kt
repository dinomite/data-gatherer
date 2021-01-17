package net.dinomite.gatherer.dp

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import net.dinomite.gatherer.model.Sensor
import net.dinomite.gatherer.services.UpdateProducer
import org.slf4j.LoggerFactory
import java.time.Duration

/**
 * Pulls information from Data Producer and packages it into EmonUpdates
 *
 * Data Producer format:
 *      [
 *          { <Sensor> },
 *          ...
 *      [
 */
class DataProducerUpdateProducer(objectMapper: ObjectMapper, dataProducerUrls: List<String>) : UpdateProducer {
    private val clients = dataProducerUrls.map { DataProducerClient(objectMapper, it) }

    override suspend fun sensors(): List<Sensor> {
        return clients
                .map { client ->
                    withContext(Dispatchers.IO) {
                        async {
                            client.retrieveData()
                                    .orEmpty()
                                    .mapNotNull {
                                        // TODO within x update cycles
                                        if (it.hasObservationWithinLast(Duration.ofMinutes(15))) {
                                            it
                                        } else {
                                            logger.info("Discarding update for ${it.name} from ${it.observations.first().timestamp}")
                                            null
                                        }
                                    }
                        }
                    }
                }
                .awaitAll()
                .flatten()
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.name)
    }
}
