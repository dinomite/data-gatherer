package net.dinomite.dg.services

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import net.dinomite.dp.DataProducerClient
import net.dinomite.dp.model.Sensor

/**
 * Pulls information from Data Producer and packages it into EmonUpdates
 *
 * Data Producer format:
 *      [
 *          {
 *              <Sensor>
 *          }
 *      [
 */
class DataProducerUpdateProducer(objectMapper: ObjectMapper, dataProducerUrls: List<String>) : UpdateProducer {
    private val clients = dataProducerUrls.map { DataProducerClient(objectMapper, it) }

    override suspend fun sensorValues(): List<Sensor> {
        // TODO remove sensors that don't reply for a while
        return clients
                .map { client ->
                    withContext(Dispatchers.IO) {
                        async {
                            client.retrieveData()
                                    .orEmpty()
                        }
                    }
                }
                .awaitAll()
                .flatten()
    }
}
