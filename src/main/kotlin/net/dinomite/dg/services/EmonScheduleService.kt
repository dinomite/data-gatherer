package net.dinomite.dg.services

import com.google.common.util.concurrent.AbstractScheduledService
import kotlinx.coroutines.runBlocking
import net.dinomite.dg.DataGathererConfig
import net.dinomite.dg.emon.EmonClient
import java.time.Duration.ZERO

@Suppress("UnstableApiUsage")
class EmonScheduleService(private val config: DataGathererConfig,
                          private val producer: EmonUpdateProducer,
                          private val emonClient: EmonClient) : AbstractScheduledService() {
    override fun scheduler(): Scheduler = Scheduler.newFixedRateSchedule(ZERO, config.SCHEDULED_SERVICE_INTERVAL)

    override fun runOneIteration() = runBlocking {
        emonClient.sendUpdate(producer.buildUpdate())
    }
}
