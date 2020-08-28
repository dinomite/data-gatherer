package net.dinomtie.gatherer.hubitat

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import net.dinomite.gatherer.hubitat.Device
import org.junit.jupiter.api.Test

internal class DeviceTest {
    private val objectMapper = jacksonObjectMapper().apply {
        registerModule(JavaTimeModule())
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    @Test
    fun deserializePowerDevice() {
        val json = """ {
                "attributes": [
                    {
                        "currentValue": 5.462,
                        "dataType": "NUMBER",
                        "name": "energy"
                    },
                    {
                        "currentValue": null,
                        "dataType": "STRING",
                        "name": "firmware"
                    },
                    {
                        "currentValue": 1,
                        "dataType": "NUMBER",
                        "name": "held"
                    },
                    {
                        "currentValue": "2020 Jan 04 Sat 2:01:01 PM",
                        "dataType": "STRING",
                        "name": "lastActivity"
                    },
                    {
                        "currentValue": " Tap \u25bc",
                        "dataType": "STRING",
                        "name": "lastEvent"
                    },
                    {
                        "currentValue": 7,
                        "dataType": "NUMBER",
                        "name": "numberOfButtons"
                    },
                    {
                        "currentValue": 0,
                        "dataType": "NUMBER",
                        "name": "power"
                    },
                    {
                        "currentValue": 1,
                        "dataType": "NUMBER",
                        "name": "pushed"
                    },
                    {
                        "currentValue": "off",
                        "dataType": "ENUM",
                        "name": "switch",
                        "values": [
                            "on",
                            "off"
                        ]
                    }
                ],
                "capabilities": [
                    "Switch",
                    {
                        "attributes": [
                            {
                                "dataType": null,
                                "name": "switch"
                            }
                        ]
                    },
                    "Configuration",
                    "Refresh",
                    "EnergyMeter",
                    {
                        "attributes": [
                            {
                                "dataType": null,
                                "name": "energy"
                            }
                        ]
                    },
                    "PowerMeter",
                    {
                        "attributes": [
                            {
                                "dataType": null,
                                "name": "power"
                            }
                        ]
                    },
                    "Sensor",
                    "Actuator",
                    "HoldableButton",
                    {
                        "attributes": [
                            {
                                "dataType": null,
                                "name": "held"
                            }
                        ]
                    },
                    "PushableButton",
                    {
                        "attributes": [
                            {
                                "dataType": null,
                                "name": "numberOfButtons"
                            },
                            {
                                "dataType": null,
                                "name": "pushed"
                            }
                        ]
                    }
                ],
                "commands": [
                    "childOff",
                    "childOn",
                    "childRefresh",
                    "configure",
                    "holdDown",
                    "holdUp",
                    "off",
                    "on",
                    "pressConfig",
                    "pressDownX1",
                    "pressDownX2",
                    "pressDownX3",
                    "pressDownX4",
                    "pressDownX5",
                    "pressUpX1",
                    "pressUpX2",
                    "pressUpX3",
                    "pressUpX4",
                    "pressUpX5",
                    "refresh",
                    "reset",
                    "setAssociationGroup"
                ],
                "id": "13",
                "label": "Porch switch",
                "name": "Porch switch"
            }
            """

        val result = objectMapper.readValue(json, Device::class.java)
        println(result)
    }

    @Test
    fun deserializeEnvironmentDevice() {
        val json = """
            {
              "id": "193",
              "name": "Main hall temperature & humidity",
              "label": "Zigbee device LUMI:lumi.weather",
              "attributes": [
                {
                  "name": "battery",
                  "currentValue": 78,
                  "dataType": "NUMBER"
                },
                {
                  "name": "batteryLastReplaced",
                  "currentValue": "Jun 04 2020",
                  "dataType": "STRING"
                },
                {
                  "name": "humidity",
                  "currentValue": 52.5,
                  "dataType": "NUMBER"
                },
                {
                  "name": "lastCheckinEpoch",
                  "currentValue": null,
                  "dataType": "STRING"
                },
                {
                  "name": "lastCheckinTime",
                  "currentValue": null,
                  "dataType": "DATE"
                },
                {
                  "name": "pressure",
                  "currentValue": 1004,
                  "dataType": "NUMBER"
                },
                {
                  "name": "temperature",
                  "currentValue": 75.94,
                  "dataType": "NUMBER"
                }
              ],
              "capabilities": [
                "TemperatureMeasurement",
                {
                  "attributes": [
                    {
                      "name": "temperature",
                      "dataType": null
                    }
                  ]
                },
                "RelativeHumidityMeasurement",
                {
                  "attributes": [
                    {
                      "name": "humidity",
                      "dataType": null
                    }
                  ]
                },
                "Battery",
                {
                  "attributes": [
                    {
                      "name": "battery",
                      "dataType": null
                    }
                  ]
                },
                "Sensor",
                "PressureMeasurement",
                {
                  "attributes": [
                    {
                      "name": "pressure",
                      "dataType": null
                    }
                  ]
                }
              ],
              "commands": [
                "resetBatteryReplacedDate"
              ]
            }
            """

        val result = objectMapper.readValue(json, Device::class.java)
        println(result)
    }
}
