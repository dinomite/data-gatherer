package net.dinomite.dg

import net.dinomite.dg.emon.EmonNode
import org.apache.commons.configuration2.CompositeConfiguration

class DataGathererConfig(config: CompositeConfiguration) {
    val emonScheme: String = config.getString("EMON_SCHEME")
    val emonHost: String = config.getString("EMON_HOST")
    val emonInputBasePath: String = config.getString("EMON_INPUT_BASE_PATH")
    val emonApiKey: String = config.getString("EMON_API_KEY")

    val hubitatScheme: String = config.getString("HUBITAT_SCHEME")
    val hubitatHost: String = config.getString("HUBITAT_HOST")
    val hubitatDeviceBasePath: String = config.getString("HUBITAT_DEVICE_BASE_PATH")
    val hubitatDevices: String = config.getList("HUBITAT_DEVICES").joinToString()
    val hubitatAccessToken: String = config.getString("HUBITAT_ACCESS_TOKEN")

    val awairEmonNode = EmonNode(config.getString("AWAIR_EMON_NODE"))
    val awairScheme: String = config.getString("AWAIR_SCHEME")
    val awairHost: String = config.getString("AWAIR_HOST")
    val awairDeviceBasePath: String = config.getString("AWAIR_DEVICE_BASE_PATH")
    val awairDeviceIds: List<String> = config.getList(String::class.java, "AWAIR_DEVICE_IDS")
    val awairAccessToken: String = config.getString("AWAIR_ACCESS_TOKEN")
}
