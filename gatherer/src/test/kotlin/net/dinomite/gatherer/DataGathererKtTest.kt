package net.dinomite.gatherer

import org.apache.commons.configuration2.CompositeConfiguration
import org.apache.commons.configuration2.MapConfiguration
import kotlin.test.Test

internal class DataGathererKtTest {
    private val objectMapper = testObjectMapper()

    @Test
    fun foo() {
        val configMap = mapOf<String, String>()
        val config = DataGathererConfig(CompositeConfiguration(MapConfiguration(configMap)))
        setupGuice(objectMapper, config)
    }
}
