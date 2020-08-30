package net.dinomite.producer.model

import net.dinomite.gatherer.model.Sensor

internal class DataProducerResponse(data: Collection<Sensor>) : Collection<Sensor> by data
