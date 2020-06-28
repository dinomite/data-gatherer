package net.dinomite.dg.data_producer

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.coroutines.awaitObjectResponseResult
import net.dinomite.dg.emon.EmonNode
import org.slf4j.LoggerFactory

class DataProducerClient(objectMapper: ObjectMapper, private val url: String) {
    private val deviceDeserializer = SensorsDeserializer(objectMapper)

    suspend fun retrieveData(): Map<EmonNode, NodeData>? {
        val (request, _, result) = Fuel.get(url)
                .awaitObjectResponseResult(deviceDeserializer)
        return result.fold(
                { it },
                { error ->
                    logger.warn("Request to ${request.url} got error ${error.exception}: ${error.message}")
                    null
                }
        )
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.name)
    }
}

class SensorsDeserializer(private val objectMapper: ObjectMapper) : ResponseDeserializable<Map<EmonNode, NodeData>> {
    companion object {
        private val typeRef = object : TypeReference<Map<EmonNode, NodeData>>() {}
    }

    override fun deserialize(content: String): Map<EmonNode, NodeData> = objectMapper.readValue(content, typeRef)
}
