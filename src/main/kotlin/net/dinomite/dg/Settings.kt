package net.dinomite.dg

import org.apache.commons.configuration2.CompositeConfiguration
import java.time.Duration

class Settings(config: CompositeConfiguration) {
    val TIME_SERVICE_INTERVAL = Duration.ofSeconds(config.getInt("TIME_SERVICE_INTERVAL_SECONDS").toLong())
}
