package net.dinomite.dg

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.inject.Inject
import net.dinomite.dg.awair.AwairClient
import net.dinomite.dg.emon.EmonClient
import net.dinomite.dg.hubitat.HubitatClient
import net.dinomite.dg.services.AwairEmonUpdateProducer
import net.dinomite.dg.services.EmonScheduleService
import net.dinomite.dg.services.HubitatPowerEmonUpdateProducer
import java.time.Duration

/**
 * Pull power updates from Hubitat and ship them off to EmonCMS.
 */
class HubitatToEmonReportingService
@Inject constructor(emonClient: EmonClient,
                    objectMapper: ObjectMapper,
                    config: DataGathererConfig) :
        EmonScheduleService(Duration.ofMinutes(1),
                HubitatPowerEmonUpdateProducer(config.HUBITAT_EMON_NODE, config.HUBITAT_DEVICES, HubitatClient(objectMapper, config)),
                emonClient)

/**
 * Pull environment data Awair and ship if off to EmonCMS.
 */
class AwairToEmonReportingService
@Inject constructor(emonClient: EmonClient,
                    objectMapper: ObjectMapper,
                    config: DataGathererConfig) :
        EmonScheduleService(Duration.ofMinutes(5),
                AwairEmonUpdateProducer(config.AWAIR_EMON_NODE, config.AWAIR_DEVICE_IDS, AwairClient(objectMapper, config)),
                emonClient)
