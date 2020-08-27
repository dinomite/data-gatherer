@file:Suppress("UnstableApiUsage")

package net.dinomite.dg

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.common.eventbus.EventBus
import com.google.inject.Inject
import net.dinomite.dg.services.DataProducerUpdateProducer
import net.dinomite.dg.services.EventBusScheduledService
import java.time.Duration

//class HubitatToEmonReportingService
//@Inject constructor(emonClient: EmonClient,
//                    objectMapper: ObjectMapper,
//                    config: DataGathererConfig) :
//        EmonScheduleService(Duration.ofMinutes(1),
//                HubitatEmonUpdateProducer(
//                        objectMapper.readValue(config.hubitatDevices),
//                        HubitatClient(objectMapper, config)
//                ),
//                emonClient)
//
//class AwairToEmonReportingService
//@Inject constructor(emonClient: EmonClient,
//                    objectMapper: ObjectMapper,
//                    config: DataGathererConfig) :
//        EmonScheduleService(Duration.ofMinutes(5),
//                AwairEmonUpdateProducer(
//                        config.awairEmonNode,
//                        config.awairDeviceIds,
//                        AwairClient(objectMapper, config)
//                ),
//                emonClient)

class DataProducerReportingService
@Inject constructor(objectMapper: ObjectMapper,
                    config: DataGathererConfig,
                    eventBus: EventBus) :
        EventBusScheduledService(Duration.ofMinutes(1),
                eventBus,
                DataProducerUpdateProducer(objectMapper, objectMapper.readValue(config.dataProducerUrls)))
