package net.dinomite.dg

import net.dinomite.dg.emon.EmonNode
import org.apache.commons.configuration2.CompositeConfiguration

class DataGathererConfig(config: CompositeConfiguration) {
    val EMON_SCHEME = config.getString("EMON_SCHEME")
    val EMON_HOST = config.getString("EMON_HOST")
    val EMON_INPUT_BASE_PATH = config.getString("EMON_INPUT_BASE_PATH")
    val EMON_API_KEY = config.getString("EMON_API_KEY")

    val HUBITAT_SCHEME = config.getString("HUBITAT_SCHEME")
    val HUBITAT_HOST = config.getString("HUBITAT_HOST")
    val HUBITAT_DEVICE_BASE_PATH = config.getString("HUBITAT_DEVICE_BASE_PATH")
    val HUBITAT_DEVICES = config.getList("HUBITAT_DEVICES").joinToString()
    val HUBITAT_ACCESS_TOKEN = config.getString("HUBITAT_ACCESS_TOKEN")

    val AWAIR_EMON_NODE = EmonNode(config.getString("AWAIR_EMON_NODE"))
    val AWAIR_SCHEME = config.getString("AWAIR_SCHEME")
    val AWAIR_HOST = config.getString("AWAIR_HOST")
    val AWAIR_DEVICE_BASE_PATH = config.getString("AWAIR_DEVICE_BASE_PATH")
    val AWAIR_DEVICE_IDS = config.getList(String::class.java, "AWAIR_DEVICE_IDS")
    val AWAIR_ACCESS_TOKEN = config.getString("AWAIR_ACCESS_TOKEN")
}
