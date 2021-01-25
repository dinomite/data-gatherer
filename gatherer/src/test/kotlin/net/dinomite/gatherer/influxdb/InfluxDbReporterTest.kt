package net.dinomite.gatherer.influxdb

import de.flapdoodle.embed.process.runtime.Network
import io.apisense.embed.influx.InfluxServer
import io.apisense.embed.influx.configuration.InfluxConfigurationWriter
import net.dinomite.gatherer.model.Group.ENVIRONMENT
import net.dinomite.gatherer.model.Observation
import net.dinomite.gatherer.model.Sensor
import org.influxdb.InfluxDBFactory
import org.influxdb.dto.Pong
import org.influxdb.dto.Query
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import java.time.Duration
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class InfluxDbReporterTest {
    private val httpPort = Network.getFreeServerPort()
    private val influxServer = InfluxServer.Builder()
        .setInfluxConfiguration(
            InfluxConfigurationWriter.Builder()
                .setHttp(httpPort)
                .build()
        )
        .build()
    private val url = "http://localhost:$httpPort"

    private val influxDb = InfluxDBFactory.connect(url)
    private val influxDbReporter = InfluxDbReporter(influxDb, mapOf("foo" to "bar"))

    @BeforeAll
    fun startInfluxDbServer() {
        influxServer.start()

        // Wait for server to start
        var tries = 0
        var ping: Pong? = null
        while (ping?.isGood == null && tries < 10) {
            try {
                ping = influxDb.ping()
            } catch (e: Exception) {
                println("Waiting for InfluxDB to start")
                Thread.sleep(1000)
            }
            tries++
        }

        if (ping?.isGood != true) {
            fail("Couldn't connect to InfluxDB")
        }
    }

    @AfterAll
    fun stopInfluxDbServer() {
        influxServer.stop()
    }

    @Test
    fun sendSensorDataToInflux() {
        val sensor = Sensor(ENVIRONMENT, "foo", Observation(7, Instant.now().minus(Duration.ofMinutes(3))))

        influxDbReporter.sendSensorDataToInflux(sensor)

        val result = influxDb
            .setDatabase(sensor.group.reportingValue())
            .query(Query("SELECT * FROM bar"))
        val actual: Any = result.results[0].series[0].values[0][1]
        assertEquals(sensor.observations.peek().value.toDouble(), actual)
    }
}
