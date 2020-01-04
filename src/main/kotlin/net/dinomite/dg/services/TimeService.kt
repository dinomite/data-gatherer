package net.dinomite.dg.services

import com.google.common.util.concurrent.AbstractScheduledService
import java.time.Duration
import java.time.Duration.ZERO
import java.time.Instant

@Suppress("UnstableApiUsage")
class TimeService(private val interval: Duration = Duration.ofMinutes(1)) : AbstractScheduledService() {
    override fun scheduler() = Scheduler.newFixedRateSchedule(ZERO, interval)

    override fun runOneIteration() {
        println("The time is ${Instant.now()}")
    }
}