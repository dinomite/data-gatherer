package net.dinomite.dg

import org.apache.commons.configuration2.CompositeConfiguration
import java.time.Duration

class DataGathererConfig(config: CompositeConfiguration) {
    val SCHEDULED_SERVICE_INTERVAL = Duration.ofSeconds(config.getInt("SCHEDULED_SERVICE_INTERVAL_SECONDS").toLong())

    val EMON_SCHEME = config.getString("EMON_SCHEME")
    val EMON_HOST = config.getString("EMON_HOST")
    val EMON_INPUT_BASE_PATH = config.getString("EMON_INPUT_BASE_PATH")
    val EMON_API_KEY = config.getString("EMON_API_KEY")
    val EMON_NODE = config.getString("EMON_NODE")

    val HUBITAT_SCHEME = config.getString("HUBITAT_SCHEME")
    val HUBITAT_HOST = config.getString("HUBITAT_HOST")
    val HUBITAT_DEVICE_BASE_PATH = config.getString("HUBITAT_DEVICE_BASE_PATH")
    val HUBITAT_DEVICES = config.getList(String::class.java, "HUBITAT_DEVICES")
    val HUBITAT_ACCESS_TOKEN = config.getString("HUBITAT_ACCESS_TOKEN")
}
