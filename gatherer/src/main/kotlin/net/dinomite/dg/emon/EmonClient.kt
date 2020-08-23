package net.dinomite.dg.emon

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.coroutines.awaitObjectResponseResult
import com.google.inject.Inject
import net.dinomite.dg.DataGathererConfig
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
    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.name)
    }

    private val emonUpdateResponseDeserializer = EmonUpdateResponseDeserializer(objectMapper)
    private val baseUrl = with(config) { "$emonScheme://$emonHost/$emonInputBasePath" }
    private val apiKey = config.emonApiKey

    override suspend fun sendUpdate(update: EmonUpdate) {
        val body = mapOf(
                "apikey" to apiKey,
                "node" to update.node.value,
                "fulljson" to objectMapper.writeValueAsString(update.updates)
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
}

class EmonUpdateResponseDeserializer(private val objectMapper: ObjectMapper) : ResponseDeserializable<EmonUpdateResponse> {
    override fun deserialize(content: String): EmonUpdateResponse =
            objectMapper.readValue(content, EmonUpdateResponse::class.java)
}
