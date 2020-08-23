package net.dinomite.dg.services

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import net.dinomite.dg.data_producer.DataProducerClient
import net.dinomite.dg.emon.EmonNode
import org.slf4j.LoggerFactory

/**
 * Pulls information from Data Producer and packages it into EmonUpdates
 *
 * Data Producer format:
 *      {
 *          "node_name": {
 *              "sensor_name": value
 *          }
 *      }
 */
class DataProducerEmonUpdateProducer(objectMapper: ObjectMapper, dataProducerUrls: List<String>) : EmonUpdateProducer {
    private val clients = dataProducerUrls.map { DataProducerClient(objectMapper, it) }

    override suspend fun buildUpdates(): List<Map<EmonNode, Map<String, String>>> {
        return clients
                .map { client ->
                    withContext(Dispatchers.IO) {
                        async {
                            client.retrieveData()
                                    .orEmpty()
                                    .map { (emonNode, nodeData) ->
                                        emonNode to nodeData.sensors
                                                .map { sensor ->
                                                    logger.debug("${sensor.name()} value ${sensor.stringValue()}")
                                                    sensor.name() to sensor.stringValue()
                                                }
                                                .toMap()
                                    }
                                    .toMap()
                        }
                    }
                }
                .awaitAll()
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.name)
    }
}
