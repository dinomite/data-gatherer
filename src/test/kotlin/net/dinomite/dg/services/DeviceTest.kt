package net.dinomite.dg.services

import net.dinomite.dg.hubitat.Device
import net.dinomite.dg.objectMapper
import org.junit.jupiter.api.Test

internal class DeviceTest {
    @Test
    fun deserialize() {
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
}