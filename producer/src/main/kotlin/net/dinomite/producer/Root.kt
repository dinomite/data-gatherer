package net.dinomite.producer

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import net.dinomite.gatherer.model.Sensor

class Root(routing: Routing, nodeDataManager: NodeDataManager) {
    init {
        routing.get("/") {
            call.respond(DataProducerResponse(nodeDataManager.getValues()))
        }
    }
}

internal class DataProducerResponse(data: Collection<Sensor>) : Collection<Sensor> by data
