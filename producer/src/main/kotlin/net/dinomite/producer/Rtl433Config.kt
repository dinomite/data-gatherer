package net.dinomite.producer

import org.apache.commons.configuration2.CompositeConfiguration

class Rtl433Config(config: CompositeConfiguration) {
    val rtl433Command: String = config.getString("RTL_433_COMMAND")
}
