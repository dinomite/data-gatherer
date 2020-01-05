package net.dinomite.dg.emon

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.coroutines.awaitObjectResult
import net.dinomite.dg.DataGathererConfig
import net.dinomite.dg.objectMapper
import org.slf4j.LoggerFactory

class EmonClient(config: DataGathererConfig) {
    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.name)
    }

    private val baseUrl = with(config) { "$EMON_SCHEME://$EMON_HOST/$EMON_INPUT_BASE_PATH" }
    private val apiKey = config.EMON_API_KEY

    suspend fun sendUpdate(update: EmonUpdate) {
        Fuel.get(baseUrl, update.parameters())
                .awaitObjectResult(EmonUpdateResponseDeserializer)
                .fold(
                        { emonUpdateResponse ->
                            if (!emonUpdateResponse.success) {
                                logger.warn("Failed to send update: ${emonUpdateResponse.message}")
                            }
                        },
                        { error -> logger.warn("An error of type ${error.exception} happened: ${error.message}") }
                )
    }

    private fun EmonUpdate.parameters(): List<Pair<String, String>> = mapOf(
            "apikey" to apiKey,
            "node" to node,
            "fulljson" to objectMapper.writeValueAsString(updates)
    ).toList()
}

object EmonUpdateResponseDeserializer : ResponseDeserializable<EmonUpdateResponse> {
    override fun deserialize(content: String): EmonUpdateResponse =
            objectMapper.readValue(content, EmonUpdateResponse::class.java)
}
