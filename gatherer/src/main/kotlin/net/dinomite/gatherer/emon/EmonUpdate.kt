package net.dinomite.gatherer.emon

data class EmonUpdate(val node: String, val updates: Map<String, String>)

