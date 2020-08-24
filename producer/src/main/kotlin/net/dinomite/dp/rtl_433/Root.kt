package net.dinomite.dp.rtl_433

import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import net.dinomite.dg.emon.EmonNode
import net.dinomite.dp.Sensor

class Root(routing: Routing, nodeDataManager: NodeDataManager) {
    init {
        routing.get("/") {
            call.respond(DataProducerResponse(mapOf(EmonNode("environment") to nodeDataManager.getValues())))
        }
    }
}

internal class DataProducerResponse(data: Map<EmonNode, Collection<Sensor>>) : Map<EmonNode, Collection<Sensor>> by data
