@file:Suppress("UnstableApiUsage")

package net.dinomite.gatherer

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.common.eventbus.EventBus
import com.google.inject.Inject
import net.dinomite.gatherer.awair.AwairClient
import net.dinomite.gatherer.awair.AwairUpdateProducer
import net.dinomite.gatherer.dp.DataProducerUpdateProducer
import net.dinomite.gatherer.hubitat.HubitatClient
import net.dinomite.gatherer.hubitat.HubitatUpdateProducer
import net.dinomite.gatherer.services.EventBusScheduledService
import java.time.Duration

class HubitatToEmonReportingService
@Inject constructor(objectMapper: ObjectMapper,
                    config: DataGathererConfig,
                    eventBus: EventBus) :
        EventBusScheduledService(Duration.ofMinutes(1),
                eventBus,
                HubitatUpdateProducer(
                        objectMapper.readValue(config.hubitatDevices),
                        HubitatClient(objectMapper, config)
                ))

class AwairToEmonReportingService
@Inject constructor(objectMapper: ObjectMapper,
                    config: DataGathererConfig,
                    eventBus: EventBus) :
        EventBusScheduledService(Duration.ofMinutes(5),
                eventBus,
                AwairUpdateProducer(
                        config.awairDeviceIds,
                        AwairClient(objectMapper, config)
                ))

class DataProducerReportingService
@Inject constructor(objectMapper: ObjectMapper,
                    config: DataGathererConfig,
                    eventBus: EventBus) :
        EventBusScheduledService(Duration.ofMinutes(1),
                eventBus,
                DataProducerUpdateProducer(objectMapper, objectMapper.readValue(config.dataProducerUrls)))