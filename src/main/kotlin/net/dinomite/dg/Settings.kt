package net.dinomite.dg

import org.apache.commons.configuration2.CompositeConfiguration
import java.time.Duration

class Settings(config: CompositeConfiguration) {
    val SCHEDULED_SERVICE_INTERVAL = Duration.ofSeconds(config.getInt("SCHEDULED_SERVICE_INTERVAL_SECONDS").toLong())

    val HUBITAT_SCHEME = config.getString("HUBITAT_SCHEME")
    val HUBITAT_HOST = config.getString("HUBITAT_HOST")
    val HUBITAT_DEVICE_BASE_PATH = config.getString("HUBITAT_DEVICE_BASE_PATH")
    val HUBITAT_DEVICES = config.getList(String::class.java, "HUBITAT_DEVICES")
    val HUBITAT_ACCESS_TOKEN = config.getString("HUBITAT_ACCESS_TOKEN")
}
