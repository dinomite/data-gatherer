package net.dinomite.dp.rtl_433

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import net.dinomite.dp.model.Sensor

class Root(routing: Routing, nodeDataManager: NodeDataManager) {
    init {
        routing.get("/") {
            call.respond(DataProducerResponse(nodeDataManager.getValues()))
        }
    }
}

internal class DataProducerResponse(data: Collection<Sensor>) : Collection<Sensor> by data
