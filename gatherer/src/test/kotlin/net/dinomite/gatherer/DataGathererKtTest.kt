package net.dinomite.gatherer

import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertNotNull

internal class DataGathererKtTest {
    private val objectMapper = testObjectMapper()

    @Test
    fun canCreateInjector() {
        val config = mockk<DataGathererConfig>(relaxed = true)

        val injector = setupGuice(objectMapper, config)

        // Verifies that wiring satisfies explicit bindings
        assertNotNull(injector)
    }
}
