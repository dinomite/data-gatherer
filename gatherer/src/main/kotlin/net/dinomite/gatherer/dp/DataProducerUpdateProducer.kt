package net.dinomite.gatherer.dp

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
 */
class DataProducerUpdateProducer(private val clients: List<DataProducerClient>) : UpdateProducer {
    override suspend fun sensors(): List<Sensor> {
        return clients
            .map { client ->
                withContext(Dispatchers.IO) {
                    async { client.transmogrify() }
                }
            }
            .awaitAll()
            .flatten()
    }

    suspend fun DataProducerClient.transmogrify(): List<Sensor> {
        return retrieveData()
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

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.name)
    }
}
