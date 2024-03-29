package net.dinomite.gatherer

import org.apache.commons.configuration2.CompositeConfiguration

class DataGathererConfig(config: CompositeConfiguration) {
    val awairScheme: String = config.getString("AWAIR_SCHEME")
    val awairHost: String = config.getString("AWAIR_HOST")
    val awairDeviceBasePath: String = config.getString("AWAIR_DEVICE_BASE_PATH")
    val awairDeviceIds: List<String> = config.getList(String::class.java, "AWAIR_DEVICE_IDS")
    val awairAccessToken: String = config.getString("AWAIR_ACCESS_TOKEN")

    val dataProducerUrls: String = config.getString("DATA_PRODUCER_URLS")

    val emonScheme: String = config.getString("EMON_SCHEME")
    val emonHost: String = config.getString("EMON_HOST")
    val emonInputBasePath: String = config.getString("EMON_INPUT_BASE_PATH")
    val emonApiKey: String = config.getString("EMON_API_KEY")

    val influxDbUrl: String = config.getString("INFLUXDB_URL")
    val influxDbUsername: String = config.getString("INFLUXDB_USERNAME")
    val influxDbPassword: String = config.getString("INFLUXDB_PASSWORD")
    val influxDbDeviceNames: String = config.getString("INFLUXDB_DEVICE_NAMES")
}
