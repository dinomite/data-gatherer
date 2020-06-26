package net.dinomite.dg.services

import net.dinomite.dg.emon.EmonUpdate

interface EmonUpdateProducer {
    /**
     * Produce a list of EmonUpdates to send to EmonCMS
     */
    suspend fun buildUpdate(): EmonUpdate
}
