package net.dinomite.gatherer.emon

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.coroutines.awaitObjectResponseResult
import com.google.inject.Inject
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import net.dinomite.gatherer.DataGathererConfig
import org.slf4j.LoggerFactory

interface EmonClient {
    suspend fun sendUpdate(update: EmonUpdate)
}

/**
 * HTTP client that sends information to EmonCMS
 */
class HttpEmonClient
@Inject constructor(private val objectMapper: ObjectMapper,
                    config: DataGathererConfig) : EmonClient {
    private val emonUpdateResponseDeserializer = EmonUpdateResponseDeserializer(objectMapper)
    private val baseUrl = with(config) { "$emonScheme://$emonHost/$emonInputBasePath" }
    private val apiKey = config.emonApiKey

    override suspend fun sendUpdate(update: EmonUpdate) {
        val body = mapOf(
                "apikey" to apiKey,
                "node" to update.node,
                "fulljson" to withContext(IO) { objectMapper.writeValueAsString(update.updates) }
        ).toList()
        val (request, _, result) = Fuel.get(baseUrl, body)
                .awaitObjectResponseResult(emonUpdateResponseDeserializer)
        result.fold(
                { emonUpdateResponse ->
                    if (!emonUpdateResponse.success) {
                        logger.warn("Failed to send update: ${emonUpdateResponse.message}")
                    }
                },
                { error -> logger.warn("Request to ${request.url} got error ${error.exception}: ${error.message}") }
        )
    }

    companion object {
        private val logger = LoggerFactory.getLogger(HttpEmonClient::class.java.name)
    }
}

class EmonUpdateResponseDeserializer(private val objectMapper: ObjectMapper) : ResponseDeserializable<EmonUpdateResponse> {
    override fun deserialize(content: String): EmonUpdateResponse =
            objectMapper.readValue(content, EmonUpdateResponse::class.java)
}