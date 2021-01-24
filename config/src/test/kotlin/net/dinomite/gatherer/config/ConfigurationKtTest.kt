package net.dinomite.gatherer.config

import org.apache.commons.configuration2.CompositeConfiguration
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.createFile
import kotlin.io.path.createTempDirectory
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@ExperimentalPathApi
internal class ConfigurationKtTest {
    private val usage = "Use it right"

    @Test
    fun argsRequired() {
        val exception = assertThrows<RuntimeException> {
            buildConfiguration(emptyList<String>().toTypedArray(), usage) {}
        }

        assertEquals("CONFIG_DIR is required\n$usage", exception.message)
    }

    @Test
    fun firstArgMustBeDirectory() {
        val path = "/does-not-exist"

        val exception = assertThrows<RuntimeException> {
            buildConfiguration(listOf(path).toTypedArray(), usage) {}
        }

        assertEquals("$path isn't a directory", exception.message)
    }

    @Test
    fun buildConfiguration_BuildsEmptyConfiguration() {
        val path = createTempDirectory("config-test")

        val config = buildConfiguration(listOf(path.toString()).toTypedArray(), usage) { TestConfig(it) }

        assertNull(config.someThing)
    }

    @Test
    fun buildConfiguration_BuildsConfiguration() {
        val path = createTempDirectory("config-test")
        File(path.toString(), "config.properties").apply {
            writeText("SOME_STRING=foobar\n")
        }

        val config = buildConfiguration(listOf(path.toString()).toTypedArray(), usage) { TestConfig(it) }

        assertEquals("foobar", config.someThing)
    }

    class TestConfig(config: CompositeConfiguration) {
        val someThing: String? = config.getString("SOME_STRING")
    }
}
