package net.dinomite.producer

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.commons.configuration2.CompositeConfiguration

class Rtl433Config(config: CompositeConfiguration) {
    val rtl433Command: String = config.getString("RTL_433_COMMAND")

    private val sensorAllowList: String = config.getString("SENSORS_ALLOWLIST")

    fun allowedSensors(objectMapper: ObjectMapper): List<String> {
        return objectMapper.readValue(
            sensorAllowList,
            objectMapper.typeFactory.constructCollectionType(List::class.java, String::class.java)
        )
    }
}
