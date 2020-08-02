package net.dinomite.dg.services

import net.dinomite.dg.emon.EmonNode

interface EmonUpdateProducer {
    /**
     * Produce a list of EmonUpdates to send to EmonCMS
     */
    suspend fun buildUpdates(): List<Map<EmonNode, Map<String, String>>>
}
