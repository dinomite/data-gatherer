package net.dinomite.dg

import com.google.common.util.concurrent.Service
import net.dinomite.dg.emon.HttpEmonClient
import net.dinomite.dg.hubitat.HubitatClient
import net.dinomite.dg.services.EmonScheduleService
import net.dinomite.dg.services.HubitatPowerEmonUpdateProducer

@Suppress("UnstableApiUsage")
fun buildServices(config: DataGathererConfig): List<Service> = listOf<Service>(
        hubitatToEmonReportingService(config)
)

/**
 * Pull power updates from Hubitat and ship them off to EmonCMS.
 */
private fun hubitatToEmonReportingService(config: DataGathererConfig): EmonScheduleService {
    val emonClient = HttpEmonClient(config)
    val hubitatClient = HubitatClient(config)
    val hubitatProducer = HubitatPowerEmonUpdateProducer(config.EMON_NODE, config.HUBITAT_DEVICES, hubitatClient)
    return EmonScheduleService(config.SCHEDULED_SERVICE_INTERVAL, hubitatProducer, emonClient)
}
