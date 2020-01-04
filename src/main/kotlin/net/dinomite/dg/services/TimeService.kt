package net.dinomite.dg.services

import com.google.common.util.concurrent.AbstractScheduledService
import net.dinomite.dg.Settings
import java.time.Duration.ZERO
import java.time.Instant

@Suppress("UnstableApiUsage")
class TimeService(private val config: Settings) : AbstractScheduledService() {
    override fun scheduler() = Scheduler.newFixedRateSchedule(ZERO, config.TIME_SERVICE_INTERVAL)

    override fun runOneIteration() {
        println("The time is ${Instant.now()}")
    }
}