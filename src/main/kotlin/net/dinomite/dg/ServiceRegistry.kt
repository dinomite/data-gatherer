package net.dinomite.dg

import com.google.common.util.concurrent.Service
import net.dinomite.dg.awair.AwairClient
import net.dinomite.dg.emon.EmonClient
import net.dinomite.dg.emon.HttpEmonClient
import net.dinomite.dg.hubitat.HubitatClient
import net.dinomite.dg.services.AwairEmonUpdateProducer
import net.dinomite.dg.services.EmonScheduleService
import net.dinomite.dg.services.HubitatPowerEmonUpdateProducer
import java.time.Duration

@Suppress("UnstableApiUsage")
fun buildServices(config: DataGathererConfig): List<Service> {
    val emonClient = HttpEmonClient(config)

    return listOf<Service>(
            hubitatToEmonReportingService(emonClient, config),
            awairToEmonReportingService(emonClient, config)
    )
}

/**
 * Pull power updates from Hubitat and ship them off to EmonCMS.
 */
private fun hubitatToEmonReportingService(emonClient: EmonClient, config: DataGathererConfig): EmonScheduleService {
    val hubitatClient = HubitatClient(config)
    val hubitatProducer = HubitatPowerEmonUpdateProducer(config.EMON_NODE, config.HUBITAT_DEVICES, hubitatClient)
    return EmonScheduleService(config.SCHEDULED_SERVICE_INTERVAL, hubitatProducer, emonClient)
}

/**
 * Pull environment data Awair and ship if off to EmonCMS.
 */
private fun awairToEmonReportingService(emonClient: EmonClient, config: DataGathererConfig): EmonScheduleService {
    val awairClient = AwairClient(config)
    // TODO set node in config
    val awairProducer = AwairEmonUpdateProducer("environment", config.AWAIR_DEVICE_IDS, awairClient)
    // TODO set interval in config
    return EmonScheduleService(Duration.ofMinutes(5), awairProducer, emonClient)
}
