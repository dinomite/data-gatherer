package net.dinomite.dg.services

import net.dinomite.dg.data_producer.DataProducerClient
import net.dinomite.dg.emon.EmonNode

/**
 * Pulls information from Data Producer and packages it into EmonUpdates
 */
class DataProducerEmonUpdateProducer(private val dataProducerClient: DataProducerClient) : EmonUpdateProducer {
    override suspend fun buildUpdate(): Map<EmonNode, Map<String, String>> {
        return dataProducerClient.retrieveData()
                .orEmpty()
                .map { (emonNode, nodeData) ->
                    emonNode to nodeData.value
                            .map { (name, sensor) ->
                                name to sensor.stringValue()
                            }
                            .toMap()
                }
                .toMap()
    }
}
