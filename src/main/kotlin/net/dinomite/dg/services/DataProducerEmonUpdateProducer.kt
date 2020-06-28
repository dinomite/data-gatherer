package net.dinomite.dg.services

import net.dinomite.dg.data_producer.DataProducerClient
import net.dinomite.dg.emon.EmonNode
import org.slf4j.LoggerFactory

/**
 * Pulls information from Data Producer and packages it into EmonUpdates
 */
class DataProducerEmonUpdateProducer(private val dataProducerClient: DataProducerClient) : EmonUpdateProducer {
    override suspend fun buildUpdate(): Map<EmonNode, Map<String, String>> {
        val data = dataProducerClient.retrieveData()
        return data
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

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.name)
    }
}
