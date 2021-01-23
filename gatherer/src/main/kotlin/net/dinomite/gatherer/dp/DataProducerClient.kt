package net.dinomite.gatherer.dp

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.coroutines.awaitObjectResponseResult
import net.dinomite.gatherer.model.Sensor
import org.slf4j.LoggerFactory

interface DataProducerClient {
    suspend fun retrieveData(): List<Sensor>?
}

class AsyncDataProducerClient(objectMapper: ObjectMapper, private val url: String) : DataProducerClient {
    private val deviceDeserializer = SensorsDeserializer(objectMapper)

    override suspend fun retrieveData(): List<Sensor>? {
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
        private val logger = LoggerFactory.getLogger(AsyncDataProducerClient::class.java.name)
    }
}

class SensorsDeserializer(private val objectMapper: ObjectMapper) : ResponseDeserializable<List<Sensor>> {
    override fun deserialize(content: String): List<Sensor> = objectMapper.readValue(content)
}
