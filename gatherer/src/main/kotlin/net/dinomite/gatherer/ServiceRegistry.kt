@file:Suppress("UnstableApiUsage")

package net.dinomite.gatherer

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.common.eventbus.EventBus
import com.google.inject.Inject
import net.dinomite.gatherer.awair.AsyncAwairClient
import net.dinomite.gatherer.awair.AwairUpdateProducer
import net.dinomite.gatherer.dp.AsyncDataProducerClient
import net.dinomite.gatherer.dp.DataProducerUpdateProducer
import net.dinomite.gatherer.services.EventBusScheduledService
import java.time.Duration

class AwairReportingService
@Inject constructor(
    objectMapper: ObjectMapper,
    config: DataGathererConfig,
    eventBus: EventBus
) :
    EventBusScheduledService(
        Duration.ofMinutes(5),
        eventBus,
        AwairUpdateProducer(
            config.awairDeviceIds,
            AsyncAwairClient(
                with(config) { "$awairScheme://$awairHost/$awairDeviceBasePath" },
                config.awairAccessToken,
                objectMapper
            )
        )
    )

class DataProducerReportingService
@Inject constructor(
    objectMapper: ObjectMapper,
    config: DataGathererConfig,
    eventBus: EventBus
) :
    EventBusScheduledService(Duration.ofMinutes(1),
        eventBus,
        DataProducerUpdateProducer(
            objectMapper.readValue<List<String>>(
                config.dataProducerUrls,
                objectMapper.typeFactory.constructCollectionType(List::class.java, String::class.java)
            ).map { AsyncDataProducerClient(objectMapper, it) }
        )
    )
