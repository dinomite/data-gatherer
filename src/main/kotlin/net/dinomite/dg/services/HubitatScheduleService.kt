package net.dinomite.dg.services

import com.google.common.util.concurrent.AbstractScheduledService
import kotlinx.coroutines.runBlocking
import net.dinomite.dg.Settings
import net.dinomite.dg.hubitat.HubitatClient
import java.time.Duration.ZERO

@Suppress("UnstableApiUsage")
class HubitatScheduleService(private val config: Settings,
                             private val hubitatClient: HubitatClient) : AbstractScheduledService() {
    private val devices = config.HUBITAT_DEVICES

    override fun scheduler(): Scheduler = Scheduler.newFixedRateSchedule(ZERO, config.SCHEDULED_SERVICE_INTERVAL)

    override fun runOneIteration() = runBlocking {
        devices.forEach { deviceId ->
            val device = hubitatClient.retrieveDevice(deviceId)
            val power = device.attributes.first { it.name == "power" }.intValue()
            println("Power usage for ${device.name}: $power")

            // TODO send to emoncms
            // http "https://emon.dinomite.net/input/post?node=emontx&apikey=99c6d3081d68d0dd5640b0c8f5e1bcb8&fulljson={\"portch_switch_power\":$VALUE}" > /dev/null
        }
    }
}
