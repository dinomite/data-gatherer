package net.dinomite.dp

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.coroutines.awaitObjectResponseResult
import net.dinomite.dp.model.Sensor
import org.slf4j.LoggerFactory

class DataProducerClient(objectMapper: ObjectMapper, private val url: String) {
    private val deviceDeserializer = SensorsDeserializer(objectMapper)

    suspend fun retrieveData(): List<Sensor>? {
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

class SensorsDeserializer(private val objectMapper: ObjectMapper) : ResponseDeserializable<List<Sensor>> {
    override fun deserialize(content: String): List<Sensor> = objectMapper.readValue(content)
}
