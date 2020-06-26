package net.dinomite.dg

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.inject.Inject
import net.dinomite.dg.awair.AwairClient
import net.dinomite.dg.emon.EmonClient
import net.dinomite.dg.hubitat.HubitatClient
import net.dinomite.dg.services.AwairEmonUpdateProducer
import net.dinomite.dg.services.EmonScheduleService
import net.dinomite.dg.services.HubitatEmonUpdateProducer
import java.time.Duration

/**
 * Pull power updates from Hubitat and ship them off to EmonCMS.
 */
class HubitatToEmonPowerReportingService
@Inject constructor(emonClient: EmonClient,
                    objectMapper: ObjectMapper,
                    config: DataGathererConfig) :
        EmonScheduleService(Duration.ofMinutes(1),
                HubitatEmonUpdateProducer(
                        objectMapper.readValue(config.hubitatDevices),
                        HubitatClient(objectMapper, config)
                ),
                emonClient)

/**
 * Pull environment data Awair and ship if off to EmonCMS.
 */
class AwairToEmonReportingService
@Inject constructor(emonClient: EmonClient,
                    objectMapper: ObjectMapper,
                    config: DataGathererConfig) :
        EmonScheduleService(Duration.ofMinutes(5),
                AwairEmonUpdateProducer(
                        config.awairEmonNode,
                        config.awairDeviceIds,
                        AwairClient(objectMapper, config)
                ),
                emonClient)
