package net.dinomite.gatherer.influxdb

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.common.eventbus.Subscribe
import com.google.inject.Inject
import net.dinomite.gatherer.DataGathererConfig
import net.dinomite.gatherer.model.Sensor
import org.influxdb.InfluxDB
import org.influxdb.InfluxDBFactory
import org.influxdb.dto.Point
import org.influxdb.dto.Query
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

/**
 * Send Sensor data to InfluxDB.  The Sensor group is used for the DB, name for the measurement, and value for the value.
 */
class InfluxDbReporter(
    private val influxDb: InfluxDB,
    private val deviceNames: Map<String, String>
) {
    @Inject
    constructor(config: DataGathererConfig, objectMapper: ObjectMapper) :
            this(
                InfluxDBFactory.connect(config.influxDbUrl, config.influxDbUsername, config.influxDbPassword),
                objectMapper.readValue(config.influxDbDeviceNames)
            )

    private val knownDbs = mutableSetOf<String>()

    @Suppress("UnstableApiUsage")
    @Subscribe
    fun sendSensorDataToInflux(sensor: Sensor) {
        logger.info("Sending ${sensor.name} to InfluxDB")
        val dbName = sensor.group.reportingValue()

        createDb(dbName)

        val observation = sensor.observations.peek()
        val name = deviceNames[sensor.name] ?: sensor.name
        influxDb.setDatabase(dbName)
            .write(
                Point.measurement(name)
                    .time(observation.timestamp.epochSecond, TimeUnit.SECONDS)
                    .addField("value", observation.value)
                    .build()
            )
    }

    /**
     * Create a database, if necessary
     */
    private fun createDb(name: String) {
        if (knownDbs.contains(name)) {
            return
        }
        val result = influxDb.query(Query("CREATE DATABASE $name"))
        if (!result.hasError()) {
            knownDbs.add(name)
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.name)
    }
}
