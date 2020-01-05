package net.dinomite.dg.services

import net.dinomite.dg.emon.EmonUpdate

interface EmonUpdateProducer {
    suspend fun buildUpdate(): EmonUpdate
}